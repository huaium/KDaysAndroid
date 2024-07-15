package com.kdays.android.ui.topicinfo


import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kdays.android.R

/**
 * The following code is based on the GlideImageGetter implementation available at:
 * https://gist.github.com/foxum/56e0cd079e47526efbbb8163d2c2ebf0#file-glideimagegetter-kt
 * Modified version for specific use case.
 */
class GlideImageGetter(
    private val targetView: TextView
) : Html.ImageGetter, Drawable.Callback {

    override fun getDrawable(url: String): Drawable {
        val wrapper = DrawableWrapper().apply {
            callback = this@GlideImageGetter
        }

        loadWithRetry(url, wrapper)

        return wrapper
    }

    private fun loadWithRetry(url: String, wrapper: DrawableWrapper, retryCount: Int = 0) {
        val maxRetryCount = 3

        Glide.with(targetView.context)
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    wrapper.setDrawable(resource)
                    // use original image size
                    wrapper.bounds = Rect(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                    wrapper.invalidateSelf()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if (retryCount < maxRetryCount) {
                        loadWithRetry(url, wrapper, retryCount + 1)
                    } else {
                        val placeholder = ContextCompat.getDrawable(
                            targetView.context,
                            R.drawable.ic_image_error
                        )
                        if (placeholder != null) {
                            wrapper.setDrawable(placeholder)
                            wrapper.bounds =
                                Rect(0, 0, placeholder.intrinsicWidth, placeholder.intrinsicHeight)
                            wrapper.invalidateSelf()
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    override fun invalidateDrawable(who: Drawable) {
        val spannable = targetView.text as? Spannable
        spannable?.getSpans<ImageSpan>()?.find { it.drawable == who }?.let {
            // this will trigger SpanWatcher in TextView
            spannable.setSpan(
                it,
                spannable.getSpanStart(it),
                spannable.getSpanEnd(it),
                spannable.getSpanFlags(it)
            )
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
    }
}