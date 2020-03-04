package com.nagarro.poptvworkshop.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.API_KEY
import com.nagarro.poptvworkshop.network.PopTvNetwork
import com.nagarro.poptvworkshop.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository() {

    private val _movieListData = MutableLiveData<List<MediaItem>>()
    val movieListData: LiveData<List<MediaItem>>
        get() = _movieListData


    private val _castListData = MutableLiveData<List<CastItem>>()
    val castListData: LiveData<List<CastItem>>
        get() = _castListData

    //Backing Property for updated data from Network
    private val _movieDetailsData = MutableLiveData<MediaItem>()
    val movieDetailsData: LiveData<MediaItem>
        get() = _movieDetailsData

    /**
     * Fetches the list of movies
     */
    suspend fun fetchMovieLists(listId: String) {
        withContext(Dispatchers.IO) {
            try {
                val mediaList =
                    PopTvNetwork.popService.getMoviesListAsync(listId, API_KEY, "en-US").await()

                withContext(Dispatchers.Main) {
                    _movieListData.value = mediaList.asDomainModel()
                }

            } catch (t: Throwable) {
                Log.e("MediaRepository", t.localizedMessage)
                throw t
            }
        }
    }

    /**
     * Fetches the list of cast of a movie
     */
    suspend fun fetchCastList(movieId: String) {
        withContext(Dispatchers.IO) {
            try {
                val castList = PopTvNetwork.popService.getCastListAsync(movieId, API_KEY).await()

                withContext(Dispatchers.Main) {
                    _castListData.value = castList.asDomainModel()
                }
            } catch (t: Throwable) {
                Log.e("MediaRepository", t.localizedMessage)
            }
        }
    }

    /**
     * Fetches the details of a movie
     */
    suspend fun fetchMovieDetails(movieId: String) {
        withContext(Dispatchers.IO) {

            try {

                val movieList =
                    PopTvNetwork.popService.getMovieDetailsAsync(movieId, API_KEY).await()

                withContext(Dispatchers.Main) {
                    _movieDetailsData.value = movieList.asDomainModel()
                }
            } catch (t: Throwable) {
                Log.e("MediaRepository", t.localizedMessage)
                throw t
            }
        }
    }
}