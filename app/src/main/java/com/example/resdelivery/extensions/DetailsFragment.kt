package com.example.resdelivery.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.transition.TransitionInflater
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.resdelivery.features.detail.representation.DetailFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
fun DetailFragment.startEnterTransitionAfterLoadingImage(
    glide: RequestManager,
    imageUrl: String,
    image: ImageView
) {
    glide.load(imageUrl)
        .dontAnimate()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                startPostponedEnterTransition()
                return false
            }
        })
        .into(image)
}

//the import has to be from androidX
@ExperimentalCoroutinesApi
fun DetailFragment.setSharedElementTransitionOnEnter() {
    sharedElementEnterTransition = TransitionInflater.from(context)
        .inflateTransition(android.R.transition.move)
}