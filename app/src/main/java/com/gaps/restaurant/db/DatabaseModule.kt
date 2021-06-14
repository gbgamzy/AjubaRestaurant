package com.gaps.restaurant.db

import android.content.Context
import androidx.room.Room


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "DbReader"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMenuDAO(appDatabase: AppDatabase): MenuDAO {
        return appDatabase.menuDAO()
    }






}