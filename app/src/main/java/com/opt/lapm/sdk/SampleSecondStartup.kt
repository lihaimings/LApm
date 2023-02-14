package com.opt.lapm.sdk

import android.content.Context
import android.util.Log
import com.opt.android_startup.AndroidStartup
import com.opt.android_startup.Startup
import com.opt.android_startup.exector.ExecutorManager
import java.util.concurrent.Executor

class SampleSecondStartup : AndroidStartup<Boolean>() {
    override fun create(context: Context): Boolean? {
        Thread.sleep(500)
        return true
    }

    override fun callCreateOnMainThread(): Boolean {
        return false
    }

    override fun waitOnMainThread(): Boolean {
        return false
    }

    override fun createException(): Executor {
        return ExecutorManager.instance.cpuExecutor
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