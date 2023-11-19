package com.example.easy_image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easyImage.mediapi.model.ImageDTO
import com.easyImage.mediapi.repository.video.VideoRepositoryImpl
import com.easyImage.mediapi.service.ImageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository : VideoRepositoryImpl
): MainViewModel() {



    private val _photos = MutableLiveData<List<ImageDTO>?>()
    val photos : LiveData<List<ImageDTO>?> get() = _photos
    private var currentImageRequestPage = 1

    fun getPhotos(
        query: String = "planet",
        shouldClearPhotos : Boolean = false
    ){
        var queryText = query
        if (shouldClearPhotos) {
            _photos.value = null
            currentImageRequestPage = 1
        }

        if (queryText.isEmpty()){
            queryText = "planet"
            currentImageRequestPage = 1
        }

        val photoService: ImageService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageService::class.java)

        try {
            viewModelScope.launch {
                val photos = photoService.getPhotos(
                    key = "36463103-c2d65a399fefc8955088325ab",
                    query = queryText,
                    page = currentImageRequestPage
                )
                photos?.let {
                    it.hits?.let { newList ->

                        val lastList = newList.map{item ->
                            ImageDTO(
                                id = item.id,
                                previewURL = item.previewURL,
                                largeImageURL = item.largeImageURL,
                                fullHDURL = item.fullHDURL,
                                imageURL = item.imageURL,
                            )
                        }

                        _photos.value = (_photos.value ?: mutableListOf()).plus(lastList)

                    }

                    currentImageRequestPage += 1
                }
            }
        }catch (e : Exception){
            println(e.cause)
        }
    }

    /*

    fun getPhotos1(
        query: String = "planet",
        shouldClearPhotos : Boolean = false
    ){
        viewModelScope.launch {
            try {
                _photos.value = Resource.loading(null)
                videoRepository.getVideos() .getSingleEvent(id).collect { resource ->

                    when(resource.status){
                        Resource.Status.SUCCESS ->{
                            resource.data?.let {
                                event = resource.data
                                _eventResult.value = resource

// to get event statistic
                                if (event?.sportId != SportTypeEnum.TENNIS.sportId){
                                    getStandingStatistic()
                                    getTeamsLastStatistic()
                                    getTeamsBetweenLastStatistic()
                                    getTeamPlayers()
                                    getMissingPlayersPlayers()
                                }

                            }?: run{
                                _eventResult.value = Resource.errorModel(null)
                            }
                        }
                        Resource.Status.ERROR -> {
                            _eventResult.value = Resource.errorModel(resource.errorModel)
                        }
                        else -> {}
                    }

                }
            } catch (ex: Exception) {
                _eventResult.value = Resource.errorModel(ex.getServiceErrorModel())
            }
        }

    }

     */
}