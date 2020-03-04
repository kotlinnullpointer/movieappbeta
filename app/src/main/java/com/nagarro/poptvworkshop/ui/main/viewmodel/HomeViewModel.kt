package com.nagarro.poptvworkshop.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nagarro.poptvworkshop.domain.CarousalItem
import com.nagarro.poptvworkshop.domain.CastItem
import com.nagarro.poptvworkshop.domain.MediaItem
import com.nagarro.poptvworkshop.network.PopTvApiStatus
import com.nagarro.poptvworkshop.repository.MediaRepository
import com.nagarro.poptvworkshop.utils.CarousalData
import com.nagarro.poptvworkshop.utils.isInternetConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _carousalPortraitList = MutableLiveData<List<MediaItem>>()
    val carousalLatestList: LiveData<List<MediaItem>>
        get() = _carousalPortraitList

    private val _carousalTallList = MutableLiveData<List<MediaItem>>()
    val carousalSciFiList: LiveData<List<MediaItem>>
        get() = _carousalTallList

    private val _carousalWideList = MutableLiveData<List<MediaItem>>()
    val carousalRomanceList: LiveData<List<MediaItem>>
        get() = _carousalWideList


    private val _carousalRoundedList = MutableLiveData<List<CastItem>>()
    val carousalCelebList: LiveData<List<CastItem>>
        get() = _carousalRoundedList

    private val _carousalAnimated = MutableLiveData<List<MediaItem>>()
    val carousalAnimatedList: LiveData<List<MediaItem>>
        get() = _carousalAnimated


    private val _navigateToDetailsPage = MutableLiveData<CarousalItem>()
    val navigateToDetailsPage: LiveData<CarousalItem>
        get() = _navigateToDetailsPage

    fun navigateToDetailsPage(item: CarousalItem) {
        _navigateToDetailsPage.value = item
    }

    fun navigateToDetailsPageDone() {
        _navigateToDetailsPage.value = null
    }

    private var isConnected: Boolean = true

    private val _apiStatus = MutableLiveData<PopTvApiStatus>()
    val apiStatus: LiveData<PopTvApiStatus>
        get() = _apiStatus

    init {
       loadAll()
    }

    /**
     * Created as a function to allow RELOAD from Home page
     * if there was no Network and Cache
     */
    fun loadAll(){
        isConnected = isInternetConnected()

        fetchDataForCarousalLatest(CarousalData.LATEST.itemId)//Latest
        fetchDataForCarousalSciFi(CarousalData.SCI_FI.itemId)//Sci-Fi
        fetchDataForCarousalRomance(CarousalData.ROMANCE.itemId)//Romance
        fetchDataForCarousalCast(CarousalData.DEMO_CAST.itemId)//Cast for Movie
        fetchDataForCarousalAnimated(CarousalData.ANIMATED.itemId)//Animated
    }

    /**
     * Fetch carousal data with a simple implementation of error Handling for the UI
     * It also determines if the data is loaded from Cache or the API.
     */
    private fun fetchDataForCarousalLatest(listId: String) {
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            try {
                _apiStatus.value = PopTvApiStatus.LOADING
                movieRepository.fetchMovieLists(listId)
                if (isConnected) {
                    _apiStatus.value = PopTvApiStatus.DONE
                } else {
                    _apiStatus.value = PopTvApiStatus.CACHED
                }
                _carousalPortraitList.value = movieRepository.movieListData.value
            } catch (t: Throwable) {
                if (isConnected) {
                    _apiStatus.value = PopTvApiStatus.ERROR
                } else {
                    _apiStatus.value = PopTvApiStatus.NO_NETWORK
                }
            }

        }
    }

    private fun fetchDataForCarousalSciFi(listId: String) {
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            try {
                movieRepository.fetchMovieLists(listId)
                _carousalTallList.value = movieRepository.movieListData.value
            } catch (t: Throwable) {

            }

        }
    }

    private fun fetchDataForCarousalAnimated(listId: String) {
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            try {
                movieRepository.fetchMovieLists(listId)
                _carousalAnimated.value = movieRepository.movieListData.value
            } catch (t: Throwable) {

            }

        }
    }

    private fun fetchDataForCarousalRomance(listId: String) {
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            try {
                movieRepository.fetchMovieLists(listId)
                _carousalWideList.value = movieRepository.movieListData.value
            } catch (t: Throwable) {

            }

        }
    }

    private fun fetchDataForCarousalCast(movieId: String) {
        val movieRepository = MediaRepository()

        viewModelScope.launch {
            movieRepository.fetchCastList(movieId)
            _carousalRoundedList.value = movieRepository.castListData.value
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
