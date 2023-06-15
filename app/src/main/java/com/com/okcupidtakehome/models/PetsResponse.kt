package com.com.okcupidtakehome.models

import com.google.gson.annotations.SerializedName

data class PetsResponse(
    @SerializedName("data")
    var data: List<Pet>
)

data class Pet(
    @SerializedName("age")
    var age: Int,
    @SerializedName("is_online")
    var isOnline: Int,
    @SerializedName("location")
    var location: Location,
    @SerializedName("liked")
    var liked: Boolean,
    @SerializedName("match")
    var match: Int,
    @SerializedName("photo")
    var photo: Photo,
    @SerializedName("userid")
    var userId: String,
    @SerializedName("username")
    var userName: String,
) {
    val matchPerc: Int get() = (match / 100f).toInt()
}

data class Location(
    @SerializedName("city_name")
    var cityName: String,
    @SerializedName("country_code")
    var countryCode: String,
    @SerializedName("country_name")
    var countryName: String,
    @SerializedName("state_code")
    var stateCode: String,
    @SerializedName("state_name")
    var stateName: String,
)

data class Photo(
    @SerializedName("large")
    var large: String,
    @SerializedName("medium")
    var medium: String,
    @SerializedName("original")
    var original: String,
    @SerializedName("small")
    var small: String,
)
