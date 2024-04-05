package dswd.ziacare.app.utils

import android.content.Context
import android.widget.ImageView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import dswd.ziacare.app.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions


// TODO Replace placeholder and error 'R.color.color_primary' to the app's default image resource

/**
 * Load with center fitCenter
 */
fun ImageView.loadAvatar(url: String?, context: Context) {
    val requestOption = RequestOptions()
        .placeholder(R.drawable.img_profile)
        .fallback(R.drawable.img_profile)
        .error(R.drawable.img_profile)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    Glide.with(context)
        .load(url)
        .thumbnail(Glide.with(this)
            .load(url)
            .apply(requestOption))
        .apply(requestOption)
        .into(this)
}

fun ImageView.loadGroupAvatar(url: String?) {
    this.load(url){
        placeholder(R.color.color_accent)
        error(R.color.color_accent)
        scale(Scale.FILL)
    }
}

/**
 * Load with center crop
 */
fun ImageView.loadImage(url: String?, context: Context) {
    val requestOption = RequestOptions()
        .placeholder(R.color.color_primary)
        .error(R.color.color_primary)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    Glide.with(context)
        .load(url)
        .thumbnail(Glide.with(this)
            .load(url)
            .apply(requestOption))
        .apply(requestOption)
        .into(this)
}