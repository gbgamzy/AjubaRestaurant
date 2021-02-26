package com.example.restaurant.db

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.restaurant.classes.*

import java.util.concurrent.locks.Lock


@Database (entities = [FoodMenu::class,Food::class,Address::class,DbOrder::class], version = 1,exportSchema = false)
@TypeConverters(TypeConverter::class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun menuDAO():MenuDAO




}