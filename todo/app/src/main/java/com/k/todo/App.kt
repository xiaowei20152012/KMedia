package com.k.todo

import android.app.Application
import android.content.res.Configuration
import android.os.AsyncTask
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (context == null) {
            context = this
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute { initProgress() }
    }

    private fun initProgress() {
        //firebase crash
        Fabric.with(this, Crashlytics())
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    companion object {

        var context: Application? = null
    }

}
