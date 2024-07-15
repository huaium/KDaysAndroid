package com.kdays.android.ui.topicinfo

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

/**
 * The following code is based on the GlideImageGetter implementation available at:
 * https://gist.github.com/foxum/ef11be2549e357ada51fb14a38f72fc2#file-drawablewrapper-kt
 * Modified version for specific use case.
 */
class DrawableWrapper : Drawable() {

    private var drawable: Drawable? = null

    fun setDrawable(drawable: Drawable) {
        this.drawable = drawable
        drawable.bounds = bounds
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        drawable?.bounds = bounds
    }

    override fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        drawable?.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable?.colorFilter = colorFilter
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.UNKNOWN", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.UNKNOWN
}