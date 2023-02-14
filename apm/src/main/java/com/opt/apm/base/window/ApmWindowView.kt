package com.opt.apm.base.window

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible


class ApmWindowView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle), IApmViewUpdate {

    private var slowMethod: TextView? = null
    private var anr: TextView? = null
    private var frameRatio: TextView? = null
    private var dropFrame: TextView? = null
    private var traversal: TextView? = null
    private var tip: TextView? = null

    private var slowCount: Int = 0
    private var anrCount: Int = 0
    private var frame: Int = 0


    init {
        initView()
    }

    private fun initView() {
        tip = createTextView()
        tip?.setPadding(0, 0, 0, 20)
        tip?.isVisible = true
        tip?.text = "性能监控"
        slowMethod = createTextView()
        anr = createTextView()
        frameRatio = createTextView()
        dropFrame = createTextView()

        gravity = Gravity.END
        orientation = VERTICAL
        addView(tip)
        addView(slowMethod)
        addView(dropFrame)
        addView(anr)
        addView(frameRatio)

    }

    private fun createTextView(): TextView {
        return TextView(context).apply {
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 3)
            isVisible = false
        }
    }

    override fun insertSlowMethodCount() {
        slowMethod?.isVisible = true
        slowCount++
        slowMethod?.text = "慢函数 =  ${slowCount} 个"
    }

    override fun insertANRCount() {
        anr?.isVisible = true
        anrCount++
        anr?.text = "ANR函数= ${anrCount} 个"
    }

    override fun updateFrameRate(frame: Int) {
        frameRatio?.isVisible = true
        frameRatio?.text = "帧率 = ${frame} fps"
    }

    override fun dropFrameCount(count: Int) {
        dropFrame?.isVisible = true
        frame++
        dropFrame?.text = "掉帧次数 = ${frame} 次"
    }

    override fun updateTraversalCost(ms: Long) {
//        traversal?.isVisible = true
//        traversal?.text = "绘制耗时 = ${ms} ms"
    }


}