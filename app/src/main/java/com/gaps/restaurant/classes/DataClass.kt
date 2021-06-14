package com.gaps.restaurant.classes

import android.graphics.Bitmap
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.Expose


data class User(


    @Keep
    var phone:String,
    @Keep
    var name:String?,

    @Keep
    var registrationToken:List<String>
)

data class Admin(
    @Keep
    var latitude:Double?,
    @Keep
    var longitude:Double?,
    @Keep
    var phone:String?,
    @Keep
    var dist1: Double?,
    @Keep
    var price1: Double?,
    @Keep
    var dist2: Double?,
    @Keep
    var price2: Double?,
    @Keep
    var dist3: Double?,
    @Keep
    var price3: Double?,
    @Keep
    var minimumDistance:Double?,
    @Keep
    var minimumPrice:Double?,
    @Keep
    var registrationToken:String?

)




data class Message(
    @Keep
        var message:String
)
data class Image(
    @Keep
    var name: String,
    @Keep
    var image:Bitmap?
    )

@Entity(tableName="cart")
data class Food(
    @Keep
        var category:String,
@PrimaryKey(autoGenerate=false)
@Keep
    var name:String,
    @Keep
    var price:Int,
    @Keep
    var image:String,
    @Keep
var quantity:Int=0,

    var available:Int?


)

@Entity(tableName="menu")
data class FoodMenu(
    @Keep
    var category:String,
    @Keep
    var list:List<Food>

){
    @PrimaryKey(autoGenerate=true)

    var id:Int=0

}

class TypeConverter{
    @TypeConverter
    fun fromFoodList(value:List<Food>?)= Gson().toJson(value)
    @TypeConverter
    fun toFoodList(value:String?)= Gson().fromJson(value,Array<Food>::class.java).toList()







}




@Entity(tableName="address")
data class Address(

        var houseName:String,
        var streetAddress:String,
        var latitude:Double,
        var longitude:Double
)
{
    @PrimaryKey(autoGenerate = true)
    var uid:Int=0
}


data class Order(
    @Keep
    var name:String?,
    @Keep
        var OID:Int?,
    @Keep
        var contents:String?,
    @Keep
        var price: Int?,
    @Keep
        var date: String?,
    @Keep
        var status:String?,
    @Keep
        var AID:Int?,
    @Keep
        var houseName:String?,
    @Keep
        var streetAddress:String?,
    @Keep
        var latitude:Double?,
    @Keep
        var longitude:Double?,
    @Keep
        var deliveryBoy:String?,

    @Keep
        var phone:String="",



        )
data class Rider(
    @Keep
    var deliveryBoyName:String?,
    @Keep
    var deliveryBoyPhone:String?,
    @Keep
    var latitude:String?,
    @Keep
    var longitude:String?,


)
@Entity(tableName="orders")
data class DbOrder(
        var OID:Int?,
        var contents:String?,
        var price: Int?,
        var date: String?,
        var status:String?,
        var AID:Int?,
        var houseName:String?,
        var streetAddress:String?,
        var latitude:Double?,
        var longitude:Double?,

        var deliveryBoy:String?,


        var phone:String="",


){
    @PrimaryKey(autoGenerate = true)
    var uid:Int=0
}


