package com.example.data.di

import com.example.data.BuildConfig
import com.example.data.BuildConfig.BASE_URL
import com.example.data.repo.OpenExchangeRatesService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): OpenExchangeRatesService {
        return retrofit.create(OpenExchangeRatesService::class.java)
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    }

    @Singleton
    @Provides
    fun providesRetrofit(moshi: Moshi): Retrofit {

        val appIdInterceptor = Interceptor { chain ->
            val url = chain.request().url().newBuilder()
                .addQueryParameter("app_id", BuildConfig.API_APP_ID)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }


        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(appIdInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}