package com.gaps.restaurant.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gaps.restaurant.classes.*


@Database (entities = [FoodMenu::class,Food::class,Address::class,DbOrder::class], version = 3,exportSchema = false)
@TypeConverters(TypeConverter::class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun menuDAO():MenuDAO




}