package com.example.polyenergy.domain

import com.squareup.moshi.Json

@Json
data class AddressInfo (
    @Json(name = "ID")
    val id: Long,

    @Json(name = "Title")
    val title: String?,

    @Json(name = "AddressLine1")
    val addressLine1: String?,

    @Json(name = "AddressLine2")
    val addressLine2: String? = null,

    @Json(name = "Town")
    val town: String?,

    @Json(name = "StateOrProvince")
    val stateOrProvince: String? = null,

    @Json(name = "Postcode")
    val postcode: String?,

    @Json(name = "CountryID")
    val countryID: Long?,

    @Json(name = "Country")
    val country: Country?,

    @Json(name = "Latitude")
    val latitude: Double,

    @Json(name = "Longitude")
    val longitude: Double,

    @Json(name = "ContactTelephone1")
    val contactTelephone1: String?,

    @Json(name = "ContactTelephone2")
    val contactTelephone2: String? = null,

    @Json(name = "ContactEmail")
    val contactEmail: String? = null,

    @Json(name = "AccessComments")
    val accessComments: String? = null,

    @Json(name = "RelatedURL")
    val relatedURL: String? = null,

    @Json(name = "Distance")
    val distance: Double?,

    @Json(name = "DistanceUnit")
    val distanceUnit: Long?
)