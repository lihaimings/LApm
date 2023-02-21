package com.opt.apm.memory.model

import android.app.Activity
import java.lang.ref.WeakReference

class DestroyedActivityInfo(val key: String?, activity: Activity?, val activityName: String?) {

    @JvmField
    val activityRef: WeakReference<Activity>

    var detectedCount = 0

    init {
        activityRef = WeakReference(activity)
    }
}