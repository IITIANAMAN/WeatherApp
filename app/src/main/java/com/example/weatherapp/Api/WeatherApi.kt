package com.example.weatherapp.Api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,

    ) : Response<WeatherModel>
}