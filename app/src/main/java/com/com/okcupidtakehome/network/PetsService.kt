package com.com.okcupidtakehome.network

import com.com.okcupidtakehome.models.PetsResponse
import retrofit2.Response
import retrofit2.http.GET

interface PetsService {
    @GET("matches.json")
    suspend fun getPets(): Response<PetsResponse>
}
