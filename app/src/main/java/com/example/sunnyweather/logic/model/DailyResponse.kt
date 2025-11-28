package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DailyResponse(val status: String, val result: Result) {
    data class Result(val daily: Daily)
    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
                     @SerializedName("life_index") val lifeIndex: LifeIndex)
    data class Temperature(val date: String, val max: Float, val min: Float)
    data class Skycon(val date: Date, val value: String)
    data class LifeIndex(
//        val coldRisk: List<LifeDescription>,
//        val carWashing: List<LifeDescription>,
//        val ultraviolet: List<LifeDescription>,
//        val dressing: List<LifeDescription>
        val ultraviolet: List<LifeDescription>,
        val carWashing: List<LifeDescription>, // 注意：JSON 中就是 carWashing（驼峰）
        val dressing: List<LifeDescription>,
        val comfort: List<LifeDescription>,
        val coldRisk: List<LifeDescription>    // JSON 中就是 coldRisk
    )
    data class LifeDescription(
//        val desc: String
        val date: Date,   // "2025-11-28T00:00+08:00"
        val index: String,  // "4" 或 "1"
        val desc: String    // "强" / "适宜" / "极易发"
    )
}