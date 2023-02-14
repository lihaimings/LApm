package com.opt.android_startup.dispatcher

import android.content.Context
import android.hardware.display.DisplayManager
import com.opt.android_startup.Startup
import com.opt.android_startup.StartupListener
import com.opt.android_startup.exector.ExecutorManager
import com.opt.android_startup.extensions.getUniqueKey
import com.opt.android_startup.manager.StartupCacheManager
import com.opt.android_startup.model.StartupSortStore
import com.opt.android_startup.run.StartupRunnable
import com.opt.android_startup.utils.StartupCostTimesUtils
import com.opt.android_startup.utils.StartupLogUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

internal class StartupManagerDispatcher(
    private val context: Context,
    private val needAwaitCount: AtomicInteger,
    private val awaitCountDownLatch: CountDownLatch?,
    private val startupSize: Int,
    private val listener: StartupListener?
) : ManagerDispatcher {
    private var count: AtomicInteger? = null

    override fun prepare() {
        count = AtomicInteger()
        StartupCostTimesUtils.clear()
    }

    override fun dispatch(startup: Startup<*>, sortStore: StartupSortStore) {
        StartupLogUtils.d { "${startup::class.java.simpleName} being dispatching, onMainThread ${startup.callCreateOnMainThread()}." }

        if (StartupCacheManager.instance.hadInitialized(startup::class.java)) {
            val result =
                StartupCacheManager.instance.obtainInitializedResult<Any>(startup::class.java)
            StartupLogUtils.d { "${startup::class.java.simpleName} was completed, result from cache." }
            notifyChildren(startup, result, sortStore)
        } else {
            val runnable = StartupRunnable(context, startup, sortStore, this)
            if (!startup.callCreateOnMainThread()) {
                startup.createException().execute(runnable)
            } else {
                runnable.run()
            }
        }

    }

    override fun notifyChildren(
        dependencyParent: Startup<*>,
        result: Any?,
        sortStore: StartupSortStore
    ) {
        if (dependencyParent.waitOnMainThread() && !dependencyParent.callCreateOnMainThread()) {
            needAwaitCount.decrementAndGet()
            awaitCountDownLatch?.countDown()
        }

        sortStore.startupChildrenMap[dependencyParent::class.java.getUniqueKey()]?.forEach {
            sortStore.startupMap[it]?.run {
                onDependenciesCompleted(dependencyParent, result)
                if (dependencyParent.manualDispatch()) {
                    dependencyParent.registerDispatcher(this)
                } else {
                    toNotify()
                }
                val size = count?.incrementAndGet() ?: 0

                if (size == startupSize){
                    StartupCostTimesUtils.printAll()
                    listener?.let {
                        ExecutorManager.instance.mainExecutor.execute {
                            it.onCompleted(StartupCostTimesUtils.mainThreadTimes, StartupCostTimesUtils.costTimesMap.values.toList())
                        }
                    }
                }

            }
        }


    }


}