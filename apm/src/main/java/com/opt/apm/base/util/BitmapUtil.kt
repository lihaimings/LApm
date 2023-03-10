package com.opt.apm.base.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView

class BitmapUtil {

    // 降低分辨率
    fun decodeSampledBitmapFromResource(res: Resources, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }



    // 选择适当的Bitmap格式
    fun decodeBitmapFromResource(res: Resources, resId: Int, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565 // 使用RGB_565格式
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (width: Int, height: Int) = options.run { outWidth to outHeight }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    // 使用缓存
    private val cache = LruCache<String, Bitmap>(10) // 设置缓存大小为10

    fun putBitmapToCache(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun getBitmapFromCache(key: String): Bitmap? {
        return cache.get(key)
    }

    // 及时回收Bitmap
    fun recycleBitmap(bitmap: Bitmap) {
        bitmap.recycle()
    }

    // 限制Bitmap的大小
    fun decodeLimitedBitmapFromResource(res: Resources, resId: Int, maxSize: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        var inSampleSize = 1
        while (options.outWidth * options.outHeight > maxSize) {
            options.outWidth /= 2
            options.outHeight /= 2
            inSampleSize *= 2
        }
        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }


}