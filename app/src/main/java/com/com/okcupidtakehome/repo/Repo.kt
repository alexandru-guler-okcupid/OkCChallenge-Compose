package com.com.okcupidtakehome.repo

import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.network.PetsService
import javax.inject.Inject

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class Repo @Inject constructor(private val service: PetsService) {

    suspend fun getPets(): Result<List<Pet>>  {
        val response = service.getPets()
        val pets = response.body()?.data
        return if (pets != null) {
            Result.Success(pets)
        } else {
            Result.Error(Exception())
        }
    }
}
