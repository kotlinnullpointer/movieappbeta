package com.nagarro.poptvworkshop.ui.main.component.carousal

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.domain.CarousalItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.PopTvApiStatus
import com.nagarro.poptvworkshop.utils.*
import com.squareup.picasso.Picasso

@BindingAdapter("setItems")
fun bindRecyclerView(recyclerView: RecyclerView, list: List<CarousalItem>?) {
    list?.let {
        val adapter = recyclerView.adapter as MediaCarousalAdapter
        adapter.submitList(list)
    }
}

@BindingAdapter("loadImageFrom", "widthDp", "isPortrait")
fun loadImages(imageView: ImageView, url: String?, widthDp: Int, isPortrait: Boolean) {
    url?.let {

        val height = (widthDp / 3) * 4
        val fullUrl = getImagePosterUrl(url)
        if (isPortrait) {
            Picasso.get().load(fullUrl).resize(widthDp.px, height.px).into(imageView)
        } else {
            val wideHeight = (widthDp / 1.7).toInt()
            Picasso.get().load(fullUrl).resize(widthDp.px, wideHeight.px).into(imageView)
        }

    }

}

@BindingAdapter("setMetaInfo")
fun prepareDetailInfoText(textView: TextView, mediaItem: MediaItem?) {
    mediaItem?.let {
        val runTime = getTimeInHours(mediaItem.duration)
        val genreString =
            mediaItem.genres.take(2).map { it.name }.joinToString(separator = " & ") { it }
        val year = mediaItem.releaseDate?.subSequence(0, 4)

        textView.text =
            textView.context.getString(R.string.label_detail, runTime, year.toString(), genreString)
        textView.visibility = View.VISIBLE
    }
}

@BindingAdapter("apiLoading")
fun bindStatusLoading(statusImageView: View, status: PopTvApiStatus?) {
    when (status) {
        PopTvApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE

        }

        else -> statusImageView.visibility = View.GONE
    }
}

@BindingAdapter("apiError")
fun bindStatusError(statusImageView: View, status: PopTvApiStatus?) {
    when (status) {
        PopTvApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE

        }

        else -> statusImageView.visibility = View.GONE
    }
}

@BindingAdapter("apiSuccess")
fun bindStatusMainView(view: View, status: PopTvApiStatus?) {
    when (status) {
        PopTvApiStatus.NO_NETWORK, PopTvApiStatus.CACHED -> {
            view.visibility = View.VISIBLE

        }

        else -> view.visibility = View.GONE
    }
}
