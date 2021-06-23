package com.example.oil_brent_price_tracker.newsDataCollection


import java.util.*
import kotlin.collections.ArrayList

class newsData : ArrayList<newsDataItem>()

data class newsDataItem(
    val label: String,
    val score: Double,

)