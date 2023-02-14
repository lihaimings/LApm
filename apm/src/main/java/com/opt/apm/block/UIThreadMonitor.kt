package com.opt.apm.block

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Choreographer
import com.opt.apm.base.util.ApmUtil
import com.opt.apm.base.reflect.ReflectUtils
import java.lang.AssertionError
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Method
import java.util.HashSet

/**
 * 主线程的监控
 * 即监听Looper中的printer，又监听Choreographer中的input animation traversal
 * Looper和Choreographer结合计算主线程的：方法耗时和帧耗时
 */
object UIThreadMonitor {

    @Volatile
    var isAlive = false  // 是否已经开启监测

    // 注册UI线程监控的订阅者集合
    private val observers: HashSet<UIThreadMonitorObserver?> = HashSet<UIThreadMonitorObserver?>()

    private val TAG = "UIThreadMonitor"

    /**
     * Choreographer
     */
    // 注册的三种类型
    private const val CALLBACK_INPUT = 0
    private const val CALLBACK_ANIMATION = 1
    private const val CALLBACK_TRAVERSAL = 2
    private val DO_QUEUE_END_ERROR: Long = -100
    private val CALLBACK_LAST: Int = CALLBACK_TRAVERSAL

    private var queueStatus = IntArray(CALLBACK_LAST + 1)  // 记录类型的执行状态
    private var callbackExist = BooleanArray(CALLBACK_LAST + 1) // 记录此类型是否已经添加
    private var queueCost = LongArray(CALLBACK_LAST + 1) // 记录各个类型花费的时间
    private const val DO_QUEUE_DEFAULT = 0
    private const val DO_QUEUE_BEGIN = 1
    private const val DO_QUEUE_END = 2

    // 主线程的Choreographer实例
    private var choreographer: Choreographer? = null

    // 利用反射获取Choreographer的变量和方法
    private var callbackQueueLock: Any? = null
    private var callbackQueues: Array<Any>? = null
    private var addTraversalQueue: Method? = null  // Traversal类型的注册方法
    private var addInputQueue: Method? = null // Input类型的注册方法
    private var addAnimationQueue: Method? = null // Animation类型的注册方法
    private var vsyncReceiver: Any? = null
    var frameIntervalNanos: Long = 16666666

    private var isVsyncFrame = false // 是否为Vsync信号回调

    /**
     * Looper
     */
    // 记录Looper的开始和结束的时间
    private var dispatchTimeMs = LongArray(4)

    // 记录 Looper分发的消息开始的时间
    @Volatile
    private var token = 0L


    /***
     * 开始监测主线程
     */
    fun startMonitor() {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            throw AssertionError("must be init in main thread!")
        }

        // 添加主线程的Looper的分发
        LooperMonitor.addDispatchListener(looperDispatch)

        // 初始化并向Choreographer注册Callback
        initAddCallbackByChoreographer()

    }

    /***
     * 结束监测主线程
     */
    fun stopMonitor() {
        if (isAlive) {
            isAlive = false
            LooperMonitor.removeDispatchListener(looperDispatch)
        }
    }

    /**
     * 增加订阅者，会自动开始监控
     */
    fun addObserver(observer: UIThreadMonitorObserver) {
        if (!isAlive) {
            startMonitor()
        }
        synchronized(observers) {
            observers.add(observer)
        }
    }

    /**
     * 删除订阅者，会自动结束监控
     */
    fun removeObserver(observer: UIThreadMonitorObserver) {
        synchronized(observers) {
            observers.remove(observer)
            if (observers.size <= 0) {
                stopMonitor()
            }
        }
    }

    /**
     * Looper分发的具体处理逻辑
     */
    private val looperDispatch = object : LooperMonitor.LooperDispatchListener() {
        override fun dispatchStart() {
            this@UIThreadMonitor.dispatchBegin()
        }

        override fun dispatchEnd() {
            this@UIThreadMonitor.dispatchEnd()
        }
    }

    /**
     * 初始化并向Choreographer注册Callback
     */
    private fun initAddCallbackByChoreographer() {
        // 反射获取Choreographer的变量和方法
        reflectByChoreographer()

        if (!isAlive) {
            this.isAlive = true
            synchronized(this) {
                // 初始化三个类型的数组
                callbackExist = BooleanArray(CALLBACK_LAST + 1) // 此类型是否已经注册
                queueStatus = IntArray(CALLBACK_LAST + 1) // 此类型执行的状态
                queueCost = LongArray(CALLBACK_LAST + 1) // 此类型的花费时间
                // 向Choreographer添加CALLBACK_INPUT类型的Callback
                addFrameCallback(CALLBACK_INPUT, runnable, true)
            }
        }
    }

    /**
     * 反射获取Choreographer的变量和方法
     */
    private fun reflectByChoreographer() {
        choreographer = Choreographer.getInstance()
        frameIntervalNanos =
            ReflectUtils.reflectObject(choreographer, "mFrameIntervalNanos", frameIntervalNanos)
        callbackQueueLock = ReflectUtils.reflectObject(choreographer, "mLock", Any())
        callbackQueues = ReflectUtils.reflectObject(choreographer, "mCallbackQueues", null)
        callbackQueues?.let {
            addInputQueue = ReflectUtils.reflectMethod(
                it[CALLBACK_INPUT], "addCallbackLocked",
                Long::class.javaPrimitiveType,
                Any::class.java,
                Any::class.java
            )
            addAnimationQueue = ReflectUtils.reflectMethod(
                it[CALLBACK_ANIMATION], "addCallbackLocked",
                Long::class.javaPrimitiveType,
                Any::class.java,
                Any::class.java
            )
            addTraversalQueue = ReflectUtils.reflectMethod(
                it[CALLBACK_TRAVERSAL], "addCallbackLocked",
                Long::class.javaPrimitiveType,
                Any::class.java,
                Any::class.java
            )
        }
        vsyncReceiver = ReflectUtils.reflectObject(choreographer, "mDisplayEventReceiver", null)
    }


    /**
     * 向Choreographer添加CALLBACK
     */
    @Synchronized
    private fun addFrameCallback(type: Int, callback: Runnable, isAddHeader: Boolean) {
        // callbackExist判断是否已经加入Runnable
        if (callbackExist[type]) {
            return
        }
        // 判断是否结束
        if (!isAlive && type == CALLBACK_INPUT) {
            return
        }
        // 利用反射，把对应的CALLBACK添加到链表中
        try {
            callbackQueues?.let {
                synchronized(it) {
                    var method: Method? = null
                    when (type) {
                        CALLBACK_INPUT -> method = addInputQueue
                        CALLBACK_ANIMATION -> method = addAnimationQueue
                        CALLBACK_TRAVERSAL -> method = addTraversalQueue
                    }
                    if (null != method) {
                        method.invoke(
                            it[type],
                            if (!isAddHeader) SystemClock.uptimeMillis() else -1,
                            callback,
                            null
                        )
                        callbackExist[type] = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * Choreographer中执行Input CALLBACK的回调
     */
    private val runnable = object : Runnable {
        override fun run() {
            val start = System.nanoTime()
            try {
                //isBelongFrame标志位为true,标志已经开始纳入统计
                doFrameBegin()

                // 设置CALLBACK_INPUT类型的queueStatus和queueCost
                doQueueBegin(CALLBACK_INPUT)

                // 注册CALLBACK_ANIMATION
                addFrameCallback(
                    CALLBACK_ANIMATION,
                    { // CALLBACK_ANIMATION执行表示，它前面的CALLBACK_INPUT已经执行完了
                        // 结束CALLBACK_INPUT统计，更新queueStatus，queueCost，callbackExist的值
                        doQueueEnd(CALLBACK_INPUT)
                        // 开始统计CALLBACK_ANIMATION
                        doQueueBegin(CALLBACK_ANIMATION)
                    },
                    true
                )

                // 注册CALLBACK_TRAVERSAL
                addFrameCallback(
                    CALLBACK_TRAVERSAL,
                    { // 它执行，表示前面的CALLBACK_ANIMATION已经执行完
                        doQueueEnd(CALLBACK_ANIMATION)
                        // 开始激励CALLBACK_TRAVERSAL
                        doQueueBegin(CALLBACK_TRAVERSAL)
                    },
                    true
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * input CallBack的回调开始
     * Frame的开始
     */
    private fun doFrameBegin() {
        this.isVsyncFrame = true
    }

    /**
     * TRAVERSAL CallBack的结束，用Looper的分发的结束做判断
     * Frame的结束
     */
    private fun doFrameEnd() {
        doQueueEnd(CALLBACK_TRAVERSAL)
        for (i in queueStatus) {
            if (i != DO_QUEUE_END) {
                queueCost[i] = DO_QUEUE_END_ERROR
                throw RuntimeException("UIThreadMonitor happens type[$i] != DO_QUEUE_END")
            }
        }
        queueStatus = IntArray(CALLBACK_LAST + 1)
        addFrameCallback(CALLBACK_INPUT, runnable, true)
    }

    /**
     * 每种类型的开始
     */
    private fun doQueueBegin(type: Int) {
        queueStatus[type] = DO_QUEUE_BEGIN
        queueCost[type] = System.nanoTime()
    }

    /**
     * 每种类型的结束
     */
    private fun doQueueEnd(type: Int) {
        queueStatus[type] = DO_QUEUE_END
        queueCost[type] = System.nanoTime() - queueCost[type]
        synchronized(this) {
            callbackExist[type] = false
        }
    }

    /**
     * Looper分发的Message开始
     */
    private fun dispatchBegin() {
        // 将开始时间分发给订阅者
        token = System.nanoTime()
        dispatchTimeMs[0] = token
        dispatchTimeMs[2] = SystemClock.currentThreadTimeMillis()

        synchronized(observers) {
            observers.forEach {
                it ?: return
                if (!it.isDispatchBegin) {
                    it.dispatchBegin(dispatchTimeMs[0], dispatchTimeMs[2], token)
                }
            }
        }
    }

    /**
     * Looper分发的Message结束
     */
    private fun dispatchEnd() {
        // message开始的时间
        val startNS = token
        var intendedFrameTimeNs = startNS
        if (isVsyncFrame) {
            // 如果是isVsyncFrame则调用doFrameEnd()表示最后一个CallBack执行已经结束
            doFrameEnd()
            // 开始时间用Choreographer记录的开始时间
            intendedFrameTimeNs = getIntendedFrameTimeNs(startNS)
        }

        //  message结束的时间
        val endNs = System.nanoTime()
        // 分发给订阅者
        synchronized(observers) {
            observers.forEach {
                it ?: return
                if (it.isDispatchBegin) {
                    it.doFrame(
                        ApmUtil.getTopActivityName(),
                        startNS,
                        endNs,
                        isVsyncFrame,
                        intendedFrameTimeNs,
                        queueCost[CALLBACK_INPUT],
                        queueCost[CALLBACK_ANIMATION],
                        queueCost[CALLBACK_TRAVERSAL]
                    )
                }
            }

            // 记录结束的时间
            dispatchTimeMs[3] = SystemClock.currentThreadTimeMillis()
            dispatchTimeMs[1] = System.nanoTime()

            // 分发给订阅者
            synchronized(observers) {
                observers.forEach {
                    it ?: return
                    if (it.isDispatchBegin) {
                        it.dispatchEnd(
                            dispatchTimeMs[0],
                            dispatchTimeMs[2],
                            dispatchTimeMs[1],
                            dispatchTimeMs[3],
                            token, isVsyncFrame
                        )
                    }
                }
            }

            isVsyncFrame = false
        }
    }

    /**
     * 反射获取Choreographer记录的Frame的开始时间：mTimestampNanos
     */
    private fun getIntendedFrameTimeNs(defaultValue: Long): Long {
        try {
            return ReflectUtils.reflectObject<Long>(
                vsyncReceiver,
                "mTimestampNanos",
                defaultValue
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.toString())
        }
        return defaultValue
    }

    /**
     * 主线程监控的订阅者
     */
    abstract class UIThreadMonitorObserver {
        var isDispatchBegin = false

        open fun dispatchBegin(beginNs: Long, cpuBeginNs: Long, token: Long) {
            isDispatchBegin = true
        }

        open fun doFrame(
            focusedActivity: String?,
            startNs: Long,
            endNs: Long,
            isVsyncFrame: Boolean,
            intendedFrameTimeNs: Long,
            inputCostNs: Long,
            animationCostNs: Long,
            traversalCostNs: Long
        ) {
        }

        open fun dispatchEnd(
            beginNs: Long,
            cpuBeginMs: Long,
            endNs: Long,
            cpuEndMs: Long,
            token: Long,
            isVsyncFrame: Boolean
        ) {
            isDispatchBegin = false
        }
    }

}