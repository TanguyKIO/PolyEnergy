package com.example.polyenergy.ui.carmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.polyenergy.data.OpenChargeApi
import com.example.polyenergy.domain.BackResponse
import com.example.polyenergy.domain.ChargeInfo
import com.example.polyenergy.domain.ChargesResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CarMapViewModel : ViewModel() {

    private val _result = MutableLiveData<ChargesResponse>()
    val result: LiveData<ChargesResponse> = _result

    private val _success = MutableLiveData<BackResponse>()
    val success: LiveData<BackResponse> = _success

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    fun loadCharges(
        latitude: Double,
        longitude: Double
    ) {
        coroutineScope.launch {
            try {
                val maxLat = latitude - 0.1
                val maxLong = longitude - 0.1
                val minLat = latitude + 0.1
                val minLong = longitude + 0.1
                val verbose = false
                val compact = true
                val boundBox = "($minLat,$minLong),($$maxLat,$maxLong)"
                val getCharges = OpenChargeApi.retrofitService.getOpenCharges(
                    boundBox,
                    verbose,
                    compact
                )
                try {
                    var response = getCharges.await()
                    _result.value = ChargesResponse(response)
                } catch (e: Exception) {
                    _result.value = ChargesResponse(null)
                }

            } catch (e: Exception) {
                _result.value = ChargesResponse(null)
            }
        }
    }

    fun setFavorite(charge: ChargeInfo, cookie: String) {
        coroutineScope.launch {
            val postFav = OpenChargeApi.retrofitService.postFavorites(
                charge,
                cookie
            )
            try {
                var response = postFav.await()
                _success.value = response
            } catch (e: Exception) {
                _success.value = BackResponse(null, "Erreur lors de l'ajout de favori")
            }
        }
    }
}