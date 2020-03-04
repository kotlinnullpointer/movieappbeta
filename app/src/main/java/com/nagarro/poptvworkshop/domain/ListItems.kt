package com.nagarro.poptvworkshop.domain

import android.os.Parcelable
import com.nagarro.poptvworkshop.network.MovieGenre
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CarousalItem():Parcelable

@Parcelize
data class MediaItem(
    val id: Int = 0,
    val title: String = "",
    val backdropImage: String? = "",
    val posterImage: String? = "",
    val overview: String? = "",
    val genre: List<Int> = emptyList(),
    val playtime: Long = 2,
    val releaseDate: String? = "",
    val vote: Double = 1.0,
    val contentRating: String? = "",
    val inProgress: Boolean = false,
    val currentPlayPosition: Long = 0,
    val isSeen: Boolean = false,
    val addedInWatchList: Boolean = false,
    var duration: Long = 0,
    var tagLine: String = "",
    val genres: List<MovieGenre> = emptyList()
) : CarousalItem(), Parcelable

@Parcelize
data class CastItem(
    val castId: Int = 0,
    val character: String? = "",
    val creditId: String? = "",
    val gender: Int = 0,
    val id: Int = 0,
    val name: String? = "",
    val order: Int = 0,
    val profileImage: String? = ""
) : CarousalItem(), Parcelable

