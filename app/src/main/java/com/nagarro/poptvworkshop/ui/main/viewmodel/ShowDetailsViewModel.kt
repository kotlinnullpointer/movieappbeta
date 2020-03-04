package com.nagarro.poptvworkshop.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.PopTvApiStatus
import com.nagarro.poptvworkshop.repository.MediaRepository
import com.nagarro.poptvworkshop.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShowDetailsViewModel(private val movieId: String, val context: Context) : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _movieDetailsData = MutableLiveData<MediaItem>()
    val movieDetailsData: LiveData<MediaItem>
        get() = _movieDetailsData

    private val _carousalWideList = MutableLiveData<List<MediaItem>>()
    val carousalRandomList: LiveData<List<MediaItem>>
        get() = _carousalWideList


    private val _carousalRoundedList = MutableLiveData<List<CastItem>>()
    val carousalCastList: LiveData<List<CastItem>>
        get() = _carousalRoundedList

    private val _isAddedInWatchlist = MutableLiveData<Boolean>()
    val isAddedInWatchlist: LiveData<Boolean>
        get() = _isAddedInWatchlist

    private val _isDownloaded = MutableLiveData<Boolean>()
    val isDownloaded: LiveData<Boolean>
        get() = _isDownloaded

    private val preferencesUtil: PreferencesUtil by lazy {
        PreferencesUtil.getInstance(context)
    }

    private var isConnected :Boolean = true

    private val _castApiStatus = MutableLiveData<PopTvApiStatus>()
    val castApiStatus: LiveData<PopTvApiStatus>
        get() = _castApiStatus


    init {

        isConnected = isInternetConnected()

        fetchMovieDetails()//fetch movie details
        fetchDataForCarousalRounded()//fetch this movie's cast
        fetchDataForCarousalWide(getRandomCarousal().itemId)//Romance

        checkIfAddedToWatchlist()
        checkIfDownloaded()
    }

    /**
     * Fetch the details for this Movie
     */
    private fun fetchMovieDetails() {//133284
        val movieRepository = MediaRepository()

        viewModelScope.launch {

            try {
                _castApiStatus.value = PopTvApiStatus.LOADING//set API Loading
                movieRepository.fetchMovieDetails(movieId)
                _castApiStatus.value = PopTvApiStatus.DONE//Set API DONE
                _movieDetailsData.value = movieRepository.movieDetailsData.value
            } catch (t: Throwable) {
                showToast(context , context.getString(R.string.error_details_api_msg))
                _castApiStatus.value = PopTvApiStatus.ERROR//Set API Error
            }

        }
    }


    /**
     * fetch the data for a randomly generated carousal as a suggestion.
     */
    private fun fetchDataForCarousalWide(listId: String) {//133284
        val movieRepository = MediaRepository()

        viewModelScope.launch {

            try {
                movieRepository.fetchMovieLists(listId)
                _carousalWideList.value = movieRepository.movieListData.value
            } catch (t: Throwable) {
                showToast(context, context.getString(R.string.error_api_msg))
            }

        }
    }

    /**
     * Fetch the data for this movie's Star Cast
     */
    private fun fetchDataForCarousalRounded() {//133284
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            try {
                movieRepository.fetchCastList(movieId)
                _carousalRoundedList.value = movieRepository.castListData.value
            } catch (t: Throwable) {
                showToast(context, context.getString(R.string.error_cast_api_msg))
            }
        }
    }

    /**
     * Check if this movie is added to watchlist
     */
    private fun checkIfAddedToWatchlist() {
        val savedSet = preferencesUtil.getList(PREF_KEY_WATCHLIST)
        _isAddedInWatchlist.value = savedSet?.contains(movieId)
    }

    /**
     * Check if this movie is downloaded
     */
    private fun checkIfDownloaded() {
        val savedSet = preferencesUtil.getList(PREF_KEY_DOWNLOAD)
        _isDownloaded.value = savedSet?.contains(movieId)
    }

    /**
     * Add or remove this movie to watchlist
     */
    fun updateWatchList() {
        var savedSet = preferencesUtil.getList(PREF_KEY_WATCHLIST)

        if (savedSet.isNullOrEmpty()) {//no watchlist has been created yet
            savedSet = mutableSetOf()

            savedSet.add(movieId)// create new empty set and add this movie as first item.

        } else {
            savedSet as MutableSet<String>

            /*Fetch watchlist
            Check if movie is in the list*/
            val isInTheSet = savedSet.contains(movieId)


            //add or remove based on status
            if (isInTheSet) {
                savedSet.remove(movieId)
            } else {
                savedSet.add(movieId)
            }

        }

        /*update the set in the preferences*/
        preferencesUtil.saveInPreferences(
            PREF_KEY_WATCHLIST,
            savedSet
        )

        checkIfAddedToWatchlist()//update UI
    }

    /**
     * Add or remove this movie from Downloads
     */
    fun addToDownloads() {
        var savedSet = preferencesUtil.getList(PREF_KEY_DOWNLOAD)

        if (savedSet.isNullOrEmpty()) {//no watchlist as such
            savedSet = mutableSetOf()

            savedSet.add(movieId)// create new empty set and add this movie as first item.

        } else {

            savedSet as MutableSet<String>

            /*Fetch watchlist
            Check if movie is in the list*/
            val isInTheSet = savedSet.contains(movieId)

            //add or remove based on status
            if (isInTheSet) {
                savedSet.remove(movieId)
            } else {
                savedSet.add(movieId)
            }

        }

        /*update the set in the preferences*/
        preferencesUtil.saveInPreferences(
            PREF_KEY_DOWNLOAD,
            savedSet
        )

        checkIfDownloaded()//update UI
    }

    /**
     * Factory for constructing ShowDetailsViewModel with a parameter
     */
    class Factory(private val movieId: String, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ShowDetailsViewModel(movieId, context) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

