package com.example.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)

        } else {
            Result.failure(
                RuntimeException("response status is${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
//        coroutineScope {
//            val deferredRealtime = async {
//                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
//            }
//            val deferredDaily = async {
//                SunnyWeatherNetwork.getDailyWeather(lng, lat)
//            }
//            val realtimeResponse = deferredRealtime.await()
//            val dailyResponse = deferredDaily.await()
//            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
//                val weather = Weather(realtimeResponse.result.realtime,
//                    dailyResponse.result.daily)
//                Result.success(weather)
//            } else {
//                Result.failure(
//                    RuntimeException(
//                        "realtime response status is ${realtimeResponse.status}" +
//                                "daily response status is ${dailyResponse.status}"
//                    )
//                )
//            }
//
//        }

        try {
            // 先请求实时天气
            val realtimeResponse = SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            if (realtimeResponse.status != "ok") {
                return@fire Result.failure(
                    RuntimeException("Realtime weather failed: ${realtimeResponse.status}")
                )
            }

            // 稍等一下
            kotlinx.coroutines.delay(1000) // 👈 关键：加间隔

            // 再请求每日预报
            val dailyResponse = SunnyWeatherNetwork.getDailyWeather(lng, lat)
            Log.d("dailyResponse", "$dailyResponse")
            if (dailyResponse.status != "ok") {
                return@fire Result.failure(
                    RuntimeException("Daily weather failed: ${dailyResponse.status}")
                )
            }

            val weather = Weather(
                realtime = realtimeResponse.result.realtime,
                daily = dailyResponse.result.daily
            )
            Result.success(weather)

        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}