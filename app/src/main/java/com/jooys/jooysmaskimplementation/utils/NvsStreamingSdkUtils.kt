package com.jooys.jooysmaskimplementation.utils

import android.content.Context
import com.meicam.sdk.NvsStreamingContext

object NvsStreamingSdkUtils {

    fun initializeStreamingContext(context: Context): NvsStreamingContext? {
        var mStreamingContext: NvsStreamingContext?
        synchronized(NvsStreamingContext::class.java) {
            mStreamingContext = NvsStreamingContext.getInstance()
            if (mStreamingContext == null) {
                mStreamingContext = initStreamingContext(context, null)
            }
        }
        if (mStreamingContext != null) jlog("Streaming SDK initialized.")
        return mStreamingContext
    }

    fun initStreamingContext(mContext: Context?, licensePath: String?): NvsStreamingContext? {
        NvsStreamingContext.setDebugLevel(5)
        return NvsStreamingContext.init(
            mContext,
            licensePath,
            NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT or
                    NvsStreamingContext.STREAMING_CONTEXT_FLAG_ENABLE_HDR_DISPLAY_WHEN_SUPPORTED or
                    NvsStreamingContext.STREAMING_CONTEXT_FLAG_INTERRUPT_STOP_FOR_INTERNAL_STOP
        )
    }



}