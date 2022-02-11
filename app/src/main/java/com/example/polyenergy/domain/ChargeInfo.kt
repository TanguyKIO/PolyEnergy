package com.example.polyenergy.domain

import com.squareup.moshi.Json

data class ChargeInfo(
    @Json(name = "AddressInfo")
    val addressInfo: AddressInfo,

    @Json(name = "ID")
    val idString: Long,

    @Json(name = "DateLastStatusUdpate")
    val date: String?,
)