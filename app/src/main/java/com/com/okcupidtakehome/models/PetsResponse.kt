package com.com.okcupidtakehome.models

import com.google.gson.annotations.SerializedName

data class PetsResponse(
    @SerializedName("data")
    val data: List<Pet>
)

data class Pet(
    @SerializedName("age")
    val age: Int,
    @SerializedName("is_online")
    val isOnline: Int,
    @SerializedName("location")
    val location: Location,
    @SerializedName("liked")
    val liked: Boolean,
    @SerializedName("match")
    val match: Int,
    @SerializedName("photo")
    val photo: Photo,
    @SerializedName("userid")
    val userId: String,
    @SerializedName("username")
    val userName: String,
) {
    val matchPerc: Int get() = (match / 100f).toInt()
}

data class Location(
    @SerializedName("city_name")
    val cityName: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("state_code")
    val stateCode: String,
    @SerializedName("state_name")
    val stateName: String,
)

data class Photo(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("original")
    val original: String,
    @SerializedName("small")
    val small: String,
)
