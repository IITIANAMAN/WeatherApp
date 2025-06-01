package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Api.Confidential
import com.example.weatherapp.Api.NetworkResponse
import com.example.weatherapp.Api.RetrofitInstance
import com.example.weatherapp.Api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherData = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherData: LiveData<NetworkResponse<WeatherModel>> = _weatherData
    fun getDate(city: String)
    {
        _weatherData.value = NetworkResponse.Loading
        viewModelScope.launch{
            val response = weatherApi.getCurrentWeather(Confidential.apiKey,
                city)
            try {
                if(response.isSuccessful)
                {
                    response.body()?.let {
                        _weatherData.value = NetworkResponse.Success(it)
                    }
                }else
                {
                    _weatherData.value = NetworkResponse.Error("Enable to fetch the data")
                }
            }
            catch (e: Exception)
            {
                _weatherData.value = NetworkResponse.Error("Error fetching weather data")
            }

        }
    }
}