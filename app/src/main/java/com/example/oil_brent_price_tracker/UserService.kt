package com.example.oil_brent_price_tracker

import com.example.oil_brent_price_tracker.newsDataCollection.newsDataItem
import com.example.oil_brent_price_tracker.newsDataCollection.newsData
import com.example.oil_brent_price_tracker.pricesDataCollection.pricesData
import com.example.oil_brent_price_tracker.pricesDataCollection.pricesDataItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface UserService {
    @GET("stock/tsla/chart/1m?token=pk_4f83fc972aa94accaae2459ba5ec219f")
    //@GET("users")
    fun listPrices(): Call<List<pricesDataItem>>
}



interface UserServiceNews{


    @GET("predict")

    //open fun getLocation()
    fun listSentiments(@Query(value = "review", encoded = true) text: String?): Call<List<newsDataItem>>
}