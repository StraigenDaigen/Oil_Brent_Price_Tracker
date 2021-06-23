package com.example.oil_brent_price_tracker.pricesDataCollection

import java.util.*
import kotlin.collections.ArrayList

class pricesData : ArrayList<pricesDataItem>()

data class pricesDataItem(
    val close: Double,
    val date: String,
    val id: String,
    val symbol: String,

)



