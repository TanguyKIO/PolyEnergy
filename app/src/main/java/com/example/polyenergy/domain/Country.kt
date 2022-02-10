package com.example.polyenergy.domain

import com.squareup.moshi.Json

data class Country(
    @Json(name = "ISOCode")
    val isoCode: String,

    @Json(name = "ContinentCode")
    val continentCode: String,

    @Json(name = "ID")
    val id: Long,

    @Json(name = "Title")
    val title: String
)
