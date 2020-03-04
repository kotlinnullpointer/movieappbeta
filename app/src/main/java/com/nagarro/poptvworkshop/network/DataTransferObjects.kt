package com.nagarro.poptvworkshop.network

import android.os.Parcelable
import com.nagarro.poptvworkshop.domain.*
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * This class contains all the Network Models and their extensions to convert the model
 * for Repository usage to load UI data
 */


//++++++++++++++++++++Network Containers for Json Parsing+++++++++++++++++++++
@JsonClass(generateAdapter = true)
data class NetworkMovieListContainer(
    val items: List<NetworkMovieItem>,
    val item_count: Int
)

@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkMovieDetailsContainer(
    val adult: Boolean = false,
    val backdrop_path: String = "",
    val genre_ids: List<Int> = emptyList(),
    val id: Int = 0,
    val media_type: String = "",
    val original_language: String = "",
    val original_title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val poster_path: String = "",
    val release_date: String = "",
    val title: String = "",
    val video: Boolean = false,
    val vote_average: Double = 0.0,
    val vote_count: Int = 0,
    val runtime: Long = 0,
    val tagline: String = "",
    val genres: List<MovieGenre> = emptyList()

) : Parcelable


//++++++++++++++++++++Object classes+++++++++++++++++++++

@Parcelize
data class MovieGenre(val id: Int, val name: String) : Parcelable

@Parcelize
open class NetworkMovieItem(
    val adult: Boolean = false,
    val backdrop_path: String = "",
    val genre_ids: List<Int> = emptyList(),
    open val id: Int = 0,
    val media_type: String = "",
    val original_language: String = "",
    val original_title: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val poster_path: String = "",
    val release_date: String = "",
    val title: String = "",
    val video: Boolean = false,
    val vote_average: Double = 0.0,
    val vote_count: Int = 0,
    val runtime: Long = 0,
    val tagline: String = "",
    val genres: List<MovieGenre> = emptyList()
) : Parcelable


//++++++++++++++++++++Network to Domain extensions+++++++++++++++++++++
/**
 * Convert Network results to usable VO objects
 */
fun NetworkMovieListContainer.asDomainModel(): List<MediaItem> {
    return items.map {
        MediaItem(
            title = it.title,
            id = it.id,
            backdropImage = it.backdrop_path,
            releaseDate = it.release_date,
            vote = it.vote_average,
            posterImage = it.poster_path,
            overview = it.overview
        )
    }
}

/**
 * Convert Network results to usable VO objects
 */
fun NetworkMovieDetailsContainer.asDomainModel(): MediaItem {

    return MediaItem(
        title = this.title,
        id = this.id,
        backdropImage = this.backdrop_path,
        releaseDate = this.release_date,
        vote = this.vote_average,
        posterImage = this.poster_path,
        overview = this.overview,
        duration = this.runtime,
        tagLine = this.tagline,
        genre = this.genre_ids,
        genres = this.genres
    )

}

//Cast network Container
@JsonClass(generateAdapter = true)
data class NetworkCastContainer(val cast: List<NetworkCastItem>)

@Parcelize
data class NetworkCastItem(
    val cast_id: Int,
    val character: String,
    val credit_id: String,
    val gender: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val profile_path: String
) : Parcelable


/**
 * Convert Network results to usable VO objects
 */
fun NetworkCastContainer.asDomainModel(): List<CastItem> {
    return cast.map {
        CastItem(
            id = it.id,
            castId = it.cast_id,
            character = it.character,
            gender = it.gender,
            name = it.name,
            profileImage = it.profile_path
        )
    }
}
