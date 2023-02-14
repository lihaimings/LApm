package com.opt.android_startup.run

import android.content.Context
import android.os.Process
import com.opt.android_startup.Startup
import com.opt.android_startup.annotation.ThreadPriority
import com.opt.android_startup.dispatcher.ManagerDispatcher
import com.opt.android_startup.manager.StartupCacheManager
import com.opt.android_startup.model.ResultModel
import com.opt.android_startup.model.StartupSortStore
import com.opt.android_startup.utils.StartupCostTimesUtils
import com.opt.android_startup.utils.StartupLogUtils

internal class StartupRunnable(
    private val context: Context,
    private val startup: Startup<*>,
    private val sortStore: StartupSortStore,
    private val dispatcher: ManagerDispatcher
) : Runnable {

    override fun run() {
        Process.setThreadPriority(
            startup::class.java.getAnnotation(ThreadPriority::class.java)?.priority
                ?: Process.THREAD_PRIORITY_DEFAULT
        )
        startup.toWait()
        StartupLogUtils.d { "${startup::class.java.simpleName} being create." }

        StartupCostTimesUtils.recordStart {
            Triple(
                startup::class.java,
                startup.callCreateOnMainThread(),
                startup.waitOnMainThread()
            )
        }
        val result = startup.create(context)
        StartupCostTimesUtils.recordEnd { startup::class.java }
        StartupCacheManager.instance.saveInitializedComponent(
            startup::class.java,
            ResultModel(result)
        )
        StartupLogUtils.d { "${startup::class.java.simpleName} was completed." }

        dispatcher.notifyChildren(startup, result, sortStore)
    }

}