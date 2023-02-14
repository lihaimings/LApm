package com.opt.apm.crash

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.Exception
import java.util.*

object ActivityManager {
    /**
     * 是否在前台
     */
    private var foregroundCount = 0
    private var bufferCount = 0
    private var appIsForeground = false

    /**
     * activity堆栈
     */
    private val activityStack: Stack<Activity> by lazy { Stack() }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                activityStack.add(p0)
            }

            override fun onActivityStarted(p0: Activity) {
                if (foregroundCount <= 0) {
                    appIsForeground = true
                }
                if (bufferCount < 0) {
                    bufferCount++
                } else {
                    foregroundCount++
                }
            }

            override fun onActivityResumed(p0: Activity) {

            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStopped(p0: Activity) {
                if (p0.isChangingConfigurations) {
                    bufferCount--
                } else {
                    foregroundCount--
                    if (foregroundCount <= 0) {
                        appIsForeground = false
                    }
                }
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(p0: Activity) {
                activityStack.remove(p0)
            }

        })
    }


    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        if (activityStack.empty()) {
            return
        }
        activityStack.forEach {
            it.finish()
        }
        activityStack.clear()
    }

    fun finishActivity(activity: Activity) {
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
        }
    }

    fun finishActiveActivity(activityName: String?): Boolean {
        try {
            activityName ?: return false
            var i = 0
            val size: Int = activityStack.size
            while (i < size) {
                val activity: Activity = activityStack[i]
                if (activity.javaClass.name.contains(activityName) && !activity.isFinishing) {
                    activity.finish()
                    return true
                }
                i++
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    fun isForeground(): Boolean {
        return appIsForeground
    }

}