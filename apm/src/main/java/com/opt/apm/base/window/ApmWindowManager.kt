package com.opt.apm.base.window

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.AccelerateInterpolator
import java.lang.Exception

class ApmWindowManager : IApmViewUpdate {

    private var windowManager: WindowManager? = null
    private var layoutParam: WindowManager.LayoutParams? = null
    private val displayMetrics = DisplayMetrics()
    private var view: ApmWindowView? = null
    var isShowing = false
    private var mainHandler: Handler? = null

    fun init(application: Application?) {
        application ?: return
        mainHandler = Handler(Looper.getMainLooper())
        view = ApmWindowView(application)
        initLayoutParams(application)
        view?.setOnTouchListener(object : OnTouchListener {
            var downX = 0f
            var downY = 0f
            var downOffsetX = 0
            var downOffsetY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.x
                        downY = event.y
                        downOffsetX = layoutParam!!.x
                        downOffsetY = layoutParam!!.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val moveX = event.x
                        val moveY = event.y
                        layoutParam!!.x += ((moveX - downX) / 3).toInt()
                        layoutParam!!.y += ((moveY - downY) / 3).toInt()
                        if (v != null) {
                            windowManager!!.updateViewLayout(v, layoutParam)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        val holder = PropertyValuesHolder.ofInt(
                            "trans", layoutParam!!.x,
                            if (layoutParam!!.x > displayMetrics.widthPixels / 2) displayMetrics.widthPixels - (view?.getWidth()
                                ?: 0) else 0
                        )
                        val animator: Animator = ValueAnimator.ofPropertyValuesHolder(holder)
                        (animator as ValueAnimator).addUpdateListener(AnimatorUpdateListener { animation ->
                            if (!isShowing) {
                                return@AnimatorUpdateListener
                            }
                            val value = animation.getAnimatedValue("trans") as Int
                            layoutParam!!.x = value
                            windowManager!!.updateViewLayout(v, layoutParam)
                        })
                        animator.setInterpolator(AccelerateInterpolator())
                        animator.setDuration(180).start()
                        val upOffsetX = layoutParam!!.x
                        val upOffsetY = layoutParam!!.y
                        if (Math.abs(upOffsetX - downOffsetX) <= 20 && Math.abs(upOffsetY - downOffsetY) <= 20) {
                        }
                    }
                }
                return true
            }
        })
    }

    private fun initLayoutParams(context: Context) {
        windowManager =
            context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        try {
            val metrics = DisplayMetrics()
            if (null != windowManager?.defaultDisplay) {
                windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                windowManager?.defaultDisplay?.getMetrics(metrics)
            }
            layoutParam = WindowManager.LayoutParams()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParam?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                layoutParam?.type = WindowManager.LayoutParams.TYPE_PHONE
            }
            layoutParam?.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            layoutParam?.gravity = Gravity.START or Gravity.TOP
            if (null != view) {
                layoutParam?.x = metrics.widthPixels - (view?.layoutParams?.width ?: 0) * 2
            }
            layoutParam?.y = 0
            layoutParam?.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParam?.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParam?.format = PixelFormat.TRANSPARENT
        } catch (e: Exception) {
        }
    }

    fun show() {
        mainHandler?.post {
            if (!isShowing) {
                isShowing = true
                windowManager?.addView(view, layoutParam)
            }
        }
    }

    fun dismiss() {
        mainHandler?.post {
            if (isShowing) {
                isShowing = false
                windowManager?.removeView(view)
            }
        }
    }

    override fun insertSlowMethodCount() {
        view?.insertSlowMethodCount()
    }

    override fun insertANRCount() {
        view?.insertANRCount()
    }

    override fun updateFrameRate(frame: Int) {
        view?.updateFrameRate(frame)
    }

    override fun dropFrameCount(count: Int) {
        view?.dropFrameCount(count)
    }

    override fun updateTraversalCost(ms: Long) {
        view?.updateTraversalCost(ms)
    }

}