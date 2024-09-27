package com.will.criminalintent.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.view.WindowMetrics

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    var options = BitmapFactory.Options()
    // 只获取 bitmap 的尺寸信息
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    // 计算缩放比例
    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        // 高度是多少倍
        val heightScale = srcHeight / destHeight
        // 宽度是多少倍
        val widthScale = srcWidth / destWidth
        // 使用倍数大的
        val sampleScale = if (heightScale > widthScale) {
            heightScale
        } else {
            widthScale
        }
        // 进行四舍五入操作
        inSampleSize = Math.round(sampleScale)
    }
    options = BitmapFactory.Options()
    // 设置采样率，如果采样率是 4，那么返回的图像尺寸是原图的 1/4
    options.inSampleSize = inSampleSize
    return BitmapFactory.decodeFile(path, options)
}

// 编写合理的缩放函数
// 这是一个保守估计方法，确认屏幕的尺寸，按此缩放图像
fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    val windowMatrix: WindowMetrics = activity.windowManager.currentWindowMetrics
    val rect:Rect = windowMatrix.bounds

    return getScaledBitmap(path, rect.width(), rect.height())
}
