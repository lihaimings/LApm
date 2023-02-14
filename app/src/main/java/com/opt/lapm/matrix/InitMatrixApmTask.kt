package com.opt.lapm.matrix

import android.app.Application
import com.tencent.matrix.Matrix
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.trace.tracer.FrameTracer
import com.tencent.matrix.trace.view.FrameDecorator
import com.tencent.matrix.util.MatrixLog
import java.io.File

object InitMatrixApmTask {

    fun init(application: Application) {
        try {
            MatrixLog.i("MatrixMonitor", "Start Matrix configurations.")
            val tracePlugin = configureTracePlugin(application)
            Matrix.init(
                Matrix.Builder(application)
                    .pluginListener(MatrixPluginListener(application))
                    .plugin(tracePlugin)
                    .build()
            )
            tracePlugin.frameTracer.addListener(FrameDecorator.getInstance(application))
            Matrix.with().startAllPlugins()

            MatrixLog.i("MatrixMonitor", "Matrix configurations done.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun configureTracePlugin(application: Application): TracePlugin {
        // /data/user/0/page_name/files/matrix_trace/ 此目录下打印信息
        val traceFileDir = File(application.filesDir, "matrix_trace")
        if (!traceFileDir.exists()) {
            if (traceFileDir.mkdirs()) {
                MatrixLog.e("MatrixMonitor", "failed to create traceFileDir")
            }
        }
        val anrTraceFile = File(traceFileDir, "anr_trace")
        val printTraceFile = File(traceFileDir, "print_trace")

        val traceConfig = TraceConfig.Builder()
            .enableFPS(true)
            .enableEvilMethodTrace(true)
            .enableAnrTrace(true)
            .enableStartup(true)
            .enableIdleHandlerTrace(true) // Introduced in Matrix 2.0
            .enableMainThreadPriorityTrace(true) // Introduced in Matrix 2.0
            .enableSignalAnrTrace(true) // Introduced in Matrix 2.0
            .anrTracePath(anrTraceFile.absolutePath)
            .printTracePath(printTraceFile.absolutePath)
            .splashActivities("com.youcheyihou.ftmain.ui.activity.SplashActivity")
            .isDebug(true)
            .isDevEnv(false)
            .build()
        val tracePlugin = TracePlugin(traceConfig)
        return tracePlugin
    }

}