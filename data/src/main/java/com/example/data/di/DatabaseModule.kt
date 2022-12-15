package com.example.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import database.CurrenciesDao
import database.CurrenciesDataBase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCurnenciesDataBase(@ApplicationContext context: Context): CurrenciesDataBase =
        Room.databaseBuilder(context, CurrenciesDataBase::class.java, "rates-db").build()

    @Provides
    @Singleton
    fun provideFavouriteDao(database: CurrenciesDataBase): CurrenciesDao = database.currenciesDao()
}