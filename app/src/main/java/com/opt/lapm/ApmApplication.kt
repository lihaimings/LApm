package com.opt.lapm

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Debug
import android.os.Environment
import android.os.Process
import android.os.StrictMode
import android.util.Log
import android.view.View
import com.opt.android_startup.StartupListener
import com.opt.android_startup.StartupManager
import com.opt.android_startup.model.CostTimesModel
import com.opt.android_startup.model.LoggerLevel
import com.opt.android_startup.model.StartupConfig
import com.opt.android_startup.time.StartupTime
import com.opt.apm.block.BlockPlugin
import com.opt.apm.crash.ActivityManager
import com.opt.apm.crash.MainProcessCrashMonitor
import com.opt.lapm.sdk.SampleFirstStartup
import com.opt.lapm.sdk.SampleFourStartup
import com.opt.lapm.sdk.SampleSecondStartup
import com.opt.lapm.sdk.SampleThirdStartup
import java.io.File
import kotlin.system.exitProcess
import com.opt.android_startup.time.AppMethodBeat
import com.opt.apm.memory.AppWatcher

class ApmApplication : Application() {

    // 泄漏的 View
    val leakedViews = mutableListOf<View?>()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        TimeStatisticsUtil.startTime("启动")
    }

    override fun onCreate() {
        super.onCreate()
//        InitMatrixApmTask.init(this)

        // 启动时间监控
        val startup = StartupTime()
        startup.start(this)

//        // 性能监控
//        IYourAutoBeeApm.plugins.add(BlockPlugin())
//        IYourAutoBeeApm.startAllPlugins(this)

        AppWatcher.install(this)

        // 依赖加载框架
        val config = StartupConfig.Builder()
            .setLoggerLevel(LoggerLevel.DEBUG)
            .setAwaitTimeout(12000L)
            .setListener(object : StartupListener {
                override fun onCompleted(
                    totalMainThreadCostTime: Long,
                    costTimesModels: List<CostTimesModel>
                ) {
                    Log.d("StartupTrack", "onCompleted: ${costTimesModels.size}")
                }
            }).build()
        StartupManager.Builder()
            .setConfig(config)
            .addStartup(SampleFirstStartup())
            .addStartup(SampleSecondStartup())
            .addStartup(SampleThirdStartup())
            .addStartup(SampleFourStartup())
            .build(this)
            .start()
            .await()

        // 崩溃重启
//        ActivityManager.init(this)
//        MainProcessCrashMonitor.start(object : MainProcessCrashMonitor.CrashListener {
//            override fun uncaughtException(thread: Thread, e: Throwable) {
//                reStartApp(this@ApmApplication)
//            }
//        })

        startStrictMode()

    }

    private fun reStartApp(application: Application) {
        val intent = Intent(application, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
        killCurrentProcess(false)
    }

    private fun killCurrentProcess(isThrow: Boolean) {
        Process.killProcess(Process.myPid())
        if (isThrow) {
            exitProcess(1)
        } else {
            exitProcess(0)
        }
    }


    private fun startStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskWrites()
                    .detectDiskReads()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )

        }
    }

}