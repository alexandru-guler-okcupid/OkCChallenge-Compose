package com.com.okcupidtakehome.di

import com.com.okcupidtakehome.network.PetsService
import com.com.okcupidtakehome.repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * TODO: Add this to a secrets.properties file and use [BuildConfig] to grab it.
 */
private const val API_URL = "https://static.okccdn.com/interview/"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePetsService(): PetsService = Retrofit
        .Builder()
        .client(OkHttpClient.Builder().build())
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PetsService::class.java)

    @Singleton
    @Provides
    fun provideRepo(service: PetsService): Repo = Repo(service)
}
