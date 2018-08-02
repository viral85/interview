package com.viralsonawala.articles.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


object BindingAdapters {

    /**
     * DataBinding Adapter that loads the image from the URL
     * @param imageView ImageView instance into which the image should be loaded
     * @param url URL of the image
     */
    @JvmStatic
    @BindingAdapter("url")
    fun setImageUrl(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
                .load(url)
                .into(imageView)
    }

    /**
     * DataBinding Adapter that loads the circular image from the URL
     * @param imageView ImageView instance into which the image should be loaded
     * @param url URL of the image
     * @param placeholder placeholder of the image
     */
    @JvmStatic
    @BindingAdapter("circle_image_url", "placeholder")
    fun setCircleImageUrl(imageView: ImageView, url: String?, placeholder: Drawable?) {
        Glide.with(imageView.context)
                .load(url)
                .apply(RequestOptions.circleCropTransform().placeholder(placeholder))
                .into(imageView)
    }

}