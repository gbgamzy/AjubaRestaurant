package com.example.restaurant.db


import androidx.room.*
import com.example.restaurant.classes.*


@Dao
interface MenuDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMenu(element:FoodMenu)

    @Query("SELECT * FROM menu ")
    suspend fun getMenu():List<FoodMenu>

    @Query("SELECT * FROM cart")
    suspend fun getCart():List<Food>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(element:Food)

    @Query("SELECT quantity FROM cart WHERE name=:name ")
    suspend fun getItem(name:String):Int

    @Query("DELETE FROM menu")
    suspend fun clearMenu():Unit

    @Query("DELETE FROM cart")
    suspend fun clearCart():Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAddress(element:Address):Unit

    @Query("SELECT * FROM address")
    suspend fun getAddress():List<Address>

    @Query("DELETE FROM address WHERE uid=:element")
    suspend fun deleteAddress(element:Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrder(element: DbOrder)

    @Query("SELECT * FROM orders")
    suspend fun getOrders():List<DbOrder>




}