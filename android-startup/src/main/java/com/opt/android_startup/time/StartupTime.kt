package com.opt.android_startup.time

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class StartupTime : Application.ActivityLifecycleCallbacks, IAppMethodBeatListener {

    private val TAG = "StartupTime"
    private var application: Application? = null
    private var createdTimeMap: HashMap<String, Long> = HashMap()

    private var firstScreenCost: Long? = null
    private var activityCount = 0

    fun start(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(this)
        AppMethodBeat.getInstance().addListener(this)
    }

    fun stop() {
        application?.unregisterActivityLifecycleCallbacks(this)
        AppMethodBeat.getInstance().removeListener(this)
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        activityCount++
        createdTimeMap["${activity::class.java.name}@${activity.hashCode()}"] =
            System.currentTimeMillis()
    }

    override fun onActivityFocused(activity: Activity) {
        // 第一屏的时间
        if (AppMethodBeat.attachBaseContextTime > 0) {
            firstScreenCost = System.currentTimeMillis() - AppMethodBeat.attachBaseContextTime
            AppMethodBeat.attachBaseContextTime = 0
            Log.d(TAG, "第一屏的启动时间 = ${firstScreenCost} ms")
        }

        // Activity的启动时间
        val createTime =
            createdTimeMap["${activity::class.java.name}@${activity.hashCode()}"]
        if ((createTime ?: 0) > 0) {
            Log.d(
                TAG,
                "${activity::class.java.name}启动时间 = ${System.currentTimeMillis() - (createTime ?: 0)}"
            )
            createdTimeMap.remove("${activity::class.java.simpleName}@${activity.hashCode()}")
        }
    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        activityCount--
        if (activityCount == 0) {
            stop()
        }
    }

}