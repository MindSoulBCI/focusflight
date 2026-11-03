package com.example.picosmokedemo.p7c91e42a.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.picosmokedemo.p7c91e42a.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
