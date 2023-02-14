package image

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.os.MessageQueue
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import java.lang.Exception

class MonitorImageView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr), MessageQueue.IdleHandler {

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        // 检测图片是否合法
        // bitmap 是大图，2M ，告警加载了大图， 图片大小只有 40 dp，
        // 网络或者本地的图片加载 200dp*200dp ，告警加载不合法
        // 不要影响性能，当然不能做到完全不影响，搞死循环，搞大内存，搞内存泄漏，不要影响主业务
        // 这就是为什么很多方案，需要线上线下采取不一样的策略去实现
        addImageLegalMonitor()
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)
        // 检测图片是否合法
        // bitmap 是大图，2M ，告警加载了大图， 图片大小只有 40 dp，
        // 网络或者本地的图片加载 200dp*200dp ，告警加载不合法
        // 不要影响性能，当然不能做到完全不影响，搞死循环，搞大内存，搞内存泄漏，不要影响主业务
        // 这就是为什么很多方案，需要线上线下采取不一样的策略去实现
        addImageLegalMonitor()
    }

    /**
     * 添加图片合法监控
     */
    private fun addImageLegalMonitor() {
        Looper.myQueue().removeIdleHandler(this)
        Looper.myQueue().addIdleHandler(this)
    }

    override fun queueIdle(): Boolean {
        try {
            val drawable = drawable
            val background = background
            if (drawable != null) {
                checkIsLegal(drawable, "图片")
            }
            if (background != null) {
                checkIsLegal(background, "背景")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun checkIsLegal(drawable: Drawable?, tag: String) {
        val viewWidth = measuredWidth
        val viewHeight = measuredHeight
        val drawableWidth = drawable?.intrinsicWidth ?: return
        val drawableHeight = drawable?.intrinsicHeight ?: return
        // 大小告警判断
        val imageSize: Int = calculateImageSize(drawable)
        if (imageSize > MAX_ALARM_IMAGE_SIZE) {
            Log.e(TAG, "图片加载不合法，" + tag + "大小 -> " + imageSize)
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable)
        }
        // 宽高告警判断
        if (MAX_ALARM_MULTIPLE * viewWidth < drawableWidth) {
            Log.e(TAG, "图片加载不合法, 控件宽度 -> " + viewWidth + " , " + tag + "宽度 -> " + drawableWidth)
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable)
        }
        if (MAX_ALARM_MULTIPLE * viewHeight < drawableHeight) {
            Log.e(TAG, "图片加载不合法, 控件高度 -> " + viewHeight + " , " + tag + "高度 -> " + drawableHeight)
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable)
        }
    }

    private fun calculateImageSize(drawable: Drawable?): Int {
        drawable ?: return 0
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            return bitmap.byteCount
        }
        val pixelSize = if (drawable.opacity != PixelFormat.OPAQUE) 4 else 2
        return pixelSize * drawable.intrinsicWidth * drawable.intrinsicHeight
    }

    /**
     * 处理警告
     */
    private fun dealWarning(
        drawableWidth: Int?,
        drawableHeight: Int?,
        imageSize: Int,
        drawable: Drawable?
    ) {

    }

    companion object {
        val TAG = "MonitorImageView"
        val MAX_ALARM_MULTIPLE = 2
        val MAX_ALARM_IMAGE_SIZE = 2 * 1024 * 1024
    }


}