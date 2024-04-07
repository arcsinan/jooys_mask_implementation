package com.jooys.jooysmaskimplementation.app

import android.app.Application
import com.jooys.jooysmaskimplementation.utils.NvsStreamingSdkUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NvsStreamingSdkUtils.initializeStreamingContext(this)
    }
}