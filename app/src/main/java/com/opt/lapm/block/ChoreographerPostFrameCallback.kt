package com.opt.lapm.block

import android.view.Choreographer

/**
 * 不断的向Choreographer注册FrameCallBack，监听[Vsync 信号]
 */
class ChoreographerPostFrameCallback {

    private var isStart = false
    private var lastFrameStartTime: Long = 0L
    private var callBack: MutableList<FrameCallBackTimeListener> = mutableListOf()

    private val frameCallBack = object : Choreographer.FrameCallback {
        override fun doFrame(p0: Long) {
            if (!isStart) {
                return
            }
            if (lastFrameStartTime != 0L) {
                val lastFrameCost = p0 - lastFrameStartTime
                callBack.forEach { frameCallBack ->
                    frameCallBack.doFrame(lastFrameCost, p0)
                }
            }
            lastFrameStartTime = p0
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun startPostFrameCallBack() {
        isStart = true
        Choreographer.getInstance().postFrameCallback(frameCallBack)
    }

    fun stopPostFrameCallBack() {
        isStart = false
        Choreographer.getInstance().removeFrameCallback(frameCallBack)
    }

    interface FrameCallBackTimeListener {
        fun doFrame(lastFrameCostTime: Long, frameTimeNanos: Long)
    }

    fun addFrameCallBackTimeListener(listener: FrameCallBackTimeListener) {
        callBack.add(listener)
    }

    fun removeFrameCallBackTimeListener(listener: FrameCallBackTimeListener) {
        callBack.remove(listener)
    }

}