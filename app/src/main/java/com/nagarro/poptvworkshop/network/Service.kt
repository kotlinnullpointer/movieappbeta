package com.nagarro.poptvworkshop.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nagarro.poptvworkshop.app.PopTvApplication
import com.nagarro.poptvworkshop.utils.isInternetConnected
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://api.themoviedb.org"
const val IMAGE_BASE_URL = "https://image.tmdb.org"

//HOW TO GET API KEY > https://www.themoviedb.org/settings/api
const val API_KEY = "USE_API_KEY_HERE"


/*Enum to update the API status*/
enum class PopTvApiStatus { LOADING, DONE, ERROR, NO_NETWORK,CACHED }

/**
 * A retrofit service to fetch list of movies and other details from same API endpoint.
 */

interface PopTvService {

    //Get Cast
    @GET("/3/movie/{movie_id}/credits")
    fun getCastListAsync(@Path("movie_id") movieId: String, @Query("api_key") api_key: String): Deferred<NetworkCastContainer>

    //    Get List
    @GET("/3/list/{list_id}")
    fun getMoviesListAsync(@Path("list_id") listId: String, @Query("api_key") api_key: String, @Query("language") language: String): Deferred<NetworkMovieListContainer>

    //    Get Movie Detail
    @GET("/3/movie/{movie_id}")
    fun getMovieDetailsAsync(@Path("movie_id") movieId: String, @Query("api_key") api_key: String): Deferred<NetworkMovieDetailsContainer>
}


/**
 * Build the Moshi object that Retrofit will be using, we add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(NullToEmptyStringAdapter)
    .add(KotlinJsonAdapterFactory())
    .build()


/**
 * Main entry point for network access.
 */
object PopTvNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
        .build()

    val popService: PopTvService = retrofit.create(PopTvService::class.java)
}

/*Used to properly parse the nulls in MOSHI */
object NullToEmptyStringAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextString()
        }
        reader.nextNull<Unit>()
        return ""
    }
}

const val cacheSize = (30 * 1024 * 1024).toLong()
val appCache = Cache(PopTvApplication.applicationContext().cacheDir, cacheSize)
const val timeout = 60 * 60 * 24 * 7

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .cache(appCache)
    .addInterceptor { chain ->
        var request = chain.request()

        request = if (isInternetConnected())
            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
        else
            request.newBuilder().header(
                "Cache-Control",
                "public, only-if-cached, max-stale=$timeout"
            ).build()

        chain.proceed(request)
    }
    .build()