package com.example.hloomap.utils.di

import android.content.Context
import androidx.room.Room
import com.example.hloomap.data.local.LocationDatabase
import com.example.hloomap.data.models.LocationEntity
import com.example.hloomap.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, LocationDatabase::class.java, Constants.Location_DATABASE
    ).allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideDao(db: LocationDatabase) = db.dao()

    @Provides
    @Singleton
    fun provideEntity() = LocationEntity()
}