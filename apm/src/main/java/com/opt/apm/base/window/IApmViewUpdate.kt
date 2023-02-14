package com.opt.apm.base.window

/**
 * 更新View的API
 */
interface IApmViewUpdate {
    fun insertSlowMethodCount()
    fun insertANRCount()
    fun updateFrameRate(frame: Int)
    fun dropFrameCount(count: Int)
    fun updateTraversalCost(ms: Long)
}