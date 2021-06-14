package com.gaps.restaurant.api

import com.gaps.restaurant.classes.*


import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface Network {

    @GET("/Ajuba/customer/menu")
    suspend fun getMenu():Response<List<FoodMenu>>
    @GET("/Ajuba/customer/food")
    suspend fun getFood():Response<List<Food>>
    @GET("/Ajuba/images/{img_id}")
    suspend fun getImage(@Path("img_id") id:String ):Response<ResponseBody>
    @POST("/Ajuba/customer/{phone}/{registrationToken}/{name}")
    suspend fun login(@Path("phone")phone:String,
                      @Path("registrationToken")registrationToken:String,
                      @Path("name")name:String):
            Response<Message>
    @GET("/Ajuba/customer/{phone}/orders")
    suspend fun getOrders(@Path("phone")phone:String):Response<List<Order>>

    @GET("/Ajuba/customer/rider/{phone}")
    suspend fun getRider(@Path("phone")phone:String):Response<Rider>

    @GET("/Ajuba/getAdmin")
    suspend fun getAdmin():Response<Admin>

    @POST("/Ajuba/placeOrder/{phone}")
    suspend fun placeOrder(@Path("phone")phone:String,@Body order: Order):Response<Message>







}
