package com.jooys.jooysmaskimplementation

import android.Manifest
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jooys.jooysmaskimplementation.mask.MaskInfo
import com.jooys.jooysmaskimplementation.mask.MaskType
import com.jooys.jooysmaskimplementation.mask.MaskUtils
import com.jooys.jooysmaskimplementation.mask.MaskView
import com.jooys.jooysmaskimplementation.mask.NvMaskHelper
import com.jooys.jooysmaskimplementation.mask.ZoomView
import com.jooys.jooysmaskimplementation.mask.maskInfoList
import com.jooys.jooysmaskimplementation.timeline.model.JysTimeline
import com.jooys.jooysmaskimplementation.timeline.model.JysTimelineObject
import com.jooys.jooysmaskimplementation.timeline.model.pause
import com.jooys.jooysmaskimplementation.timeline.model.rememberJysTimeline
import com.jooys.jooysmaskimplementation.timeline.model.resume
import com.jooys.jooysmaskimplementation.timeline.ui.JLiveWindow
import com.jooys.jooysmaskimplementation.ui.theme.JooysMaskImplementationTheme
import com.jooys.jooysmaskimplementation.utils.jlog
import com.jooys.jooysmaskimplementation.utils.toDp
import com.jooys.jooysmaskimplementation.utils.toPx

class MainActivity : ComponentActivity() {

    lateinit var timeline: JysTimeline

    // Media picker dialog
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // Load clip from uri
                val clip = JysTimelineObject.load(uri)
                if (null != clip) {
                    jlog("clipSize: ${clip.imageSize}")
                    timeline.addClip(clip)
                    timeline.showMaskSelectionDialog = true
                }
            }
        }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var maskEdit by remember { mutableStateOf(false) }

            timeline = rememberJysTimeline(context = LocalContext.current)


            fun selectMedia() {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }

            // Adds mask to first clip on the timeline for demo purposes
            fun addMask(maskInfo: MaskInfo) {
                timeline.selectedItemCoordinate = null
                timeline.showMaskSelectionDialog = false
                maskEdit = true
                if (timeline.selectedObject == null) timeline.selectedObject =
                    timeline.clips.first()

                timeline.selectedObject?.let { clip ->
                    clip.calculateFileRatio()

                    MaskUtils.setMaskCenter(timeline, clip)
                    timeline.maskZoomView.setMaskTypeAndInfo(
                        maskInfo.maskType, clip.maskInfoData
                    )
                    if (maskInfo.maskType == MaskType.NONE)
                        maskEdit = false
                }

            }

            // Required permissions to read media files
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            ) {
                val allGranted = it.count { item -> item.value } == it.size
                if (allGranted) {
                    jlog("Permissions granted!")
                    selectMedia()
                } else {
                    Toast.makeText(
                        timeline.context,
                        "Please authorize media permissions from the app settings menu.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }



            JooysMaskImplementationTheme {
                // A surface container using the 'background' color from the theme
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                        .background(Color.Green)
                ) {
                    // Dimensions of parent box
                    val containerWidth = maxWidth
                    val containerHeight = maxHeight
                    val containerHeightInPx = containerHeight.toPx().toInt()
                    // We create ZoomView here
                    val zoomView: ZoomView = remember {
                        // We create ZoomView and MaskView here.
                        //
                        ZoomView(
                            context = timeline.context
                        ).apply {

                            timeline.maskZoomView = this

                            val maskView = MaskView(
                                context = timeline.context
                            ).apply {
//                                layoutParams = ViewGroup.LayoutParams(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.MATCH_PARENT
//                                )
                                screenHeight = containerHeightInPx
                                // Set the background for testing purposes
                            }

                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            gravity = Gravity.CENTER
                            visibility = View.GONE


                            onDataChangeListener = object : ZoomView.OnDataChangeListener {
                                override fun onDataChanged() {
                                    timeline.selectedObject?.let {
                                        it.calculateFileRatio()
                                        MaskUtils.setMaskCenter(timeline, it)
                                        MaskUtils.applyMask(timeline, it, maskInfoData)
                                        timeline.selectedObject!!.maskInfoData = maskInfoData
                                    }

                                }

                                override fun onMaskTextClick() {
                                    TODO("Not yet implemented")
                                }
                            }
                            // Adding mask view inside of ZoomView
                            addView(maskView)

                            // Attach mask view
                            setMaskView(maskView)

                            post {
                                // Set fragment height as it is set Meicam Senior demo.
                                setVideoFragmentHeight(
                                    containerHeightInPx,
                                    timeline.liveWindow.width,
                                    timeline.liveWindow.height
                                )
                            }
                        }
                    }


                    BackHandler(maskEdit) {
                        if (maskEdit)  {
                            maskEdit = false
                        }
                    }

                    // LIVE WINDOW
                    JLiveWindow(timeline = timeline, containerWidth = containerWidth)
                        // Display ZoomView view
                        AndroidView(
                            factory = { zoomView },
                            update = {
                                if (!maskEdit) {
                                    it.clear()
                                    it.maskView.clearData()
                                }
                            }
                        )


                    // UI
                    Box(Modifier.fillMaxSize()) {
                        if (timeline.clips.isEmpty()) {
                            // Select media button
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(30.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "This app is created for demonstration purposes of Mask feature of Meishe SDK. Please click \"Select Media\" button to start.",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                                Button(onClick = {
                                    if (permissionsState.allPermissionsGranted) {
                                        selectMedia()
                                    } else {
                                        permissionsState.launchMultiplePermissionRequest()
                                    }

                                }) {
                                    Text("Select media")
                                }
                            }
                        }
                        if (timeline.clips.isNotEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(10.dp), contentAlignment = Alignment.BottomStart
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(onClick = {
                                        maskEdit = false
                                        if (timeline.isPlaying)
                                            timeline.pause()
                                        else
                                            timeline.resume()
                                    }) {
                                        Text(text = if (timeline.isPlaying) "Pause" else "Resume")
                                    }
                                    Button(onClick = { timeline.showMaskSelectionDialog = true }) {
                                        Text(text = "Add mask")
                                    }
                                }

                            }
                        }
                    }
                }
                if (timeline.showMaskSelectionDialog) {
                    // Select mask dialog
                    Dialog(onDismissRequest = { timeline.showMaskSelectionDialog = false }) {
                        Card {
                            Column(Modifier.padding(15.dp)) {
                                Text(text = "Select mask type")
                                Spacer(modifier = Modifier.height(15.dp))
                                for (maskInfo in maskInfoList) {
                                    Row(
                                        Modifier
                                            .width(150.dp)
                                            .height(60.dp)
                                            .clickable {
                                                addMask(maskInfo)
                                            },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Text(text = stringResource(id = maskInfo.name))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

