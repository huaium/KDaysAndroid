package com.kdays.android.ext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kdays.android.R

object ImageViewExts {

    fun ImageView.load(
        imageUrl: String?,
        placeholder: Int = R.drawable.ic_image_error
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .into(this)
        } else {
            setImageResource(placeholder)
        }
    }

    fun ImageView.loadRounded(
        imageUrl: String?,
        cornerRadius: Int = 30,
        placeholder: Int = R.drawable.ic_image_error
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            val requestOptions = RequestOptions().transform(
                CenterCrop(),
                RoundedCorners(cornerRadius)
            )

            Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .apply(requestOptions)
                .into(this)
        } else {
            setImageResource(placeholder)
        }
    }
}