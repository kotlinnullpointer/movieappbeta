package com.nagarro.poptvworkshop.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.nagarro.poptvworkshop.app.PopTvApplication
import com.nagarro.poptvworkshop.network.IMAGE_BASE_URL
import kotlin.random.Random


enum class CarousalData(val itemId: String) {
    ROMANCE("133280"), SCI_FI("133278"), LATEST("133284"), ANIMATED(
        "133283"
    ),
    ACTION("133777"), DEMO_CAST("359724")
}

/**
 * An object of selected carousal types.. Used in randomly generating an ID
 */
object CarousalDataObject {
    val list = listOf(
        CarousalData.ROMANCE,
        CarousalData.SCI_FI,
        CarousalData.LATEST,
        CarousalData.ANIMATED,
        CarousalData.ACTION
    )
}

/**
 * generates a random carousal id to display
 */
fun getRandomCarousal(): CarousalData {
    return CarousalDataObject.list[Random.nextInt(CarousalDataObject.list.indices.last)]
}

/**
 * Create the correct url for loading image from API
 */
fun getImagePosterUrl(itemUrl: String): String {

    return "$IMAGE_BASE_URL/t/p/w500$itemUrl"
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * converts minutes in 1h2m format for movie running length
 */
fun getTimeInHours(duration: Long): String {
    return if (duration > 0) {
        "${duration / 60}h${duration % 60}min"
    } else {
        ""
    }
}

/**
 * tells if phone is connected to a network
 */
fun isInternetConnected(): Boolean {

    val connectivityManager =
        PopTvApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {

            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting
    }
}