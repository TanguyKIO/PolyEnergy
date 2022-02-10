package com.example.polyenergy.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyenergy.data.OpenChargeApi
import com.example.polyenergy.domain.BackResponse
import com.example.polyenergy.domain.ChargeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {
    private val _result = MutableLiveData<BackResponse>()
    val result: LiveData<BackResponse> = _result

    private val _favorites = MutableLiveData<List<ChargeInfo>>()
    val favorites: LiveData<List<ChargeInfo>> = _favorites

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun deleteFavorite(charge: ChargeInfo, cookie: String) {
        coroutineScope.launch {
            val postFav = OpenChargeApi.retrofitService.postFavorites(charge,cookie)
            try {
                var response = postFav.await()
                _result.value = response
            } catch (e: Exception) {
                _result.value = BackResponse(null, "Erreur durant l'appel")
            }
        }
    }

    fun getFavorites(cookie: String) {
        coroutineScope.launch {
            val getFav = OpenChargeApi.retrofitService.getFavoritesList(cookie)
            try {
                var response = getFav.await()
                _favorites.value = response
            } catch (e: Exception) {
                //
            }
        }
    }
}