package com.opt.android_startup

import android.content.Context
import android.os.Looper
import androidx.core.os.TraceCompat
import com.opt.android_startup.annotation.MultipleProcess
import com.opt.android_startup.dispatcher.StartupManagerDispatcher
import com.opt.android_startup.manager.StartupCacheManager
import com.opt.android_startup.model.LoggerLevel
import com.opt.android_startup.model.StartupConfig
import com.opt.android_startup.model.StartupSortStore
import com.opt.android_startup.sort.TopologySort
import com.opt.android_startup.utils.ProcessUtils
import com.opt.android_startup.utils.StartupCostTimesUtils
import com.opt.android_startup.utils.StartupLogUtils
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class StartupManager private constructor(
    private val context: Context,
    private val startupList: List<AndroidStartup<*>>,
    private val needAwaitCount: AtomicInteger,
    private val config: StartupConfig
) {

    private var mAwaitCountDownLatch: CountDownLatch? = null

    private val mDefaultManagerDispatcher by lazy {
        StartupManagerDispatcher(
            context,
            needAwaitCount,
            mAwaitCountDownLatch,
            startupList.size,
            config.listener
        )
    }

    companion object {
        const val AWAIT_TIMEOUT = 10000L
    }

    init {
        StartupCacheManager.instance.saveConfig(config)
        StartupLogUtils.level = config.loggerLevel
    }

    fun start() = apply {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("start method must be call in MainThread.")
        }

        if (mAwaitCountDownLatch != null) {
            throw RuntimeException("start method repeated call.")
        }

        mAwaitCountDownLatch = CountDownLatch(needAwaitCount.get())

        if (startupList.isNullOrEmpty()) {
            StartupLogUtils.e { "startupList is empty in the current process." }
            return@apply
        }

        StartupCostTimesUtils.startTime = System.nanoTime()

        TopologySort.sort(startupList).run {
            mDefaultManagerDispatcher.prepare()
            execute(this)
        }

        if (needAwaitCount.get() <= 0) {
            StartupCostTimesUtils.endTime = System.nanoTime()
        }
    }

    /**
     * to await startup completed
     * block main thread.
     */
    fun await() {
        if (mAwaitCountDownLatch == null) {
            throw RuntimeException("must be call start method before call await method.")
        }

        val count = needAwaitCount.get()

        try {
            mAwaitCountDownLatch?.await(config.awaitTimeout, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (count > 0) {
            StartupCostTimesUtils.endTime = System.nanoTime()
        }

    }

    private fun execute(sortStore: StartupSortStore) {
        sortStore.result.forEach {
            mDefaultManagerDispatcher.dispatch(it, sortStore)
        }
    }


    class Builder {
        private var mStartupList = mutableListOf<AndroidStartup<*>>()
        private var mNeedAwaitCount = AtomicInteger()
        private var mLoggerLevel = LoggerLevel.NONE
        private var mAwaitTimeout = AWAIT_TIMEOUT
        private var mConfig: StartupConfig? = null

        fun addStartup(startup: AndroidStartup<*>) = apply {
            mStartupList.add(startup)
        }

        fun addAllStartup(list: List<AndroidStartup<*>>) = apply {
            list.forEach {
                addStartup(it)
            }
        }

        fun setConfig(config: StartupConfig?) = apply {
            mConfig = config
        }

        @Deprecated("Use setConfig() instead.")
        fun setLoggerLevel(level: LoggerLevel) = apply {
            mLoggerLevel = level
        }

        @Deprecated("Use setConfig() instead.")
        fun setAwaitTimeout(timeoutMilliSeconds: Long) = apply {
            mAwaitTimeout = timeoutMilliSeconds
        }

        fun build(context: Context): StartupManager {
            val realStartupList = mutableListOf<AndroidStartup<*>>()
            mStartupList.forEach {
                val process = it::class.java.getAnnotation(MultipleProcess::class.java)?.process ?: arrayOf()
                if (process.isNullOrEmpty() || ProcessUtils.isMultipleProcess(context, process)) {
                    realStartupList.add(it)
                    if (it.waitOnMainThread() && !it.callCreateOnMainThread()) {
                        mNeedAwaitCount.incrementAndGet()
                    }
                }
            }

            return StartupManager(
                context,
                realStartupList,
                mNeedAwaitCount,
                mConfig ?: StartupConfig.Builder()
                    .setLoggerLevel(mLoggerLevel)
                    .setAwaitTimeout(mAwaitTimeout)
                    .build()
            )
        }

    }


}