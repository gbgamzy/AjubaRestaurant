package com.example.restaurant.api

import android.graphics.Bitmap
import android.media.Image
import androidx.room.PrimaryKey
import com.example.restaurant.classes.*


import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.util.stream.DoubleStream.builder


interface Network {

    @GET("/Ajuba/customer/menu")
    suspend fun getMenu():Response<List<FoodMenu>>
    @GET("/Ajuba/images/{img_id}")
    suspend fun getImage(@Path("img_id") id:String ):Response<ResponseBody>
    @POST("/Ajuba/customer/{phone}/{registrationToken}")
    suspend fun login(@Path("phone")phone:String,@Path("registrationToken")registrationToken:String):Response<Message>
    @GET("/Ajuba/customer/{phone}/orders")
    suspend fun getOrders(@Path("phone")phone:String):Response<List<Order>>

     @GET("/Ajuba/customer/rider/{phone}")
        suspend fun getRider(@Path("phone")phone:String):Response<Rider>

        @GET("/Ajuba/getAdmin")
        suspend fun getAdmin():Response<Admin>

        @POST("/Ajuba/placeOrder/{phone}")
        suspend fun placeOrder(@Path("phone")phone:String,@Body order: Order):Response<Message>





}
