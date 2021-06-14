package com.gaps.restaurant.api


import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory


@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {

    @Provides
    fun providesBaseUrl(): String {
        return "http://ajubabhaturewala.co.in"
    }



    @Provides
    fun provideConverterFactory(): Converter.Factory {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideRetrofitClient(baseUrl: String, converterFactory: Converter.Factory): Retrofit {
        var h=OkHttpClient.Builder().connectTimeout(60,TimeUnit.SECONDS)
            .readTimeout(60,TimeUnit.SECONDS)
            .socketFactory(SocketFactory.getDefault())
            .retryOnConnectionFailure(retryOnConnectionFailure = true)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(h)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    fun provideRestApiService(retrofit: Retrofit): Network {
        return retrofit.create(Network::class.java)
    }

    /*@Provides
    @Singleton
    fun providesContext(): Application? {
        return Application()
    }*/

}