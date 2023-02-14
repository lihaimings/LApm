package com.opt.lapm.sdk

import android.content.Context
import android.util.Log
import com.opt.android_startup.AndroidStartup
import com.opt.android_startup.Startup

class SampleThirdStartup:AndroidStartup<Long>() {
    override fun create(context: Context): Long? {
        Thread.sleep(3000)
        return 10
    }

    override fun callCreateOnMainThread(): Boolean {
        return false
    }

    override fun waitOnMainThread(): Boolean {
        return false
    }

    override fun dependencies(): List<Class<out Startup<*>>>? {
        val list = mutableListOf<Class<out Startup<*>>>()
        list.add(SampleFirstStartup()::class.java)
        return list.toList()
    }

    override fun onDependenciesCompleted(startup: Startup<*>, result: Any?) {
        Log.d("SampleThirdStartup", "onDependenciesCompleted: ${startup::class.java.simpleName}, $result")
    }
}