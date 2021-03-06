package com.example.restaurant.classes

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



data class User(


    var phone:String,


    var registrationToken:List<String>
){

}
data class Admin(
    var latitude:Double?,
    var longitude:Double?,
    var phone:String?,
    var dist1: Double?,
    var price1: Double?,
    var dist2: Double?,
    var price2: Double?,
    var dist3: Double?,
    var price3: Double?,
    var minimumDistance:Double?,
    var minimumPrice:Double?
)




data class Message(
        var message:String
)
data class Image(
    var name: String,
    var image:Bitmap
    )

@Entity(tableName="cart")
data class Food(
        var category:String,
@PrimaryKey(autoGenerate=false)

    var name:String,
    var price:Int,
    var image:String,
var quantity:Int=0

){


}

@Entity(tableName="menu")
data class FoodMenu(

    var category:String,

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
        var deliveryBoyName:String?,
        var deliveryBoyPhone:String?,


        var phone:String="",



        )
data class Rider(
    var deliveryBoyName:String?,
    var deliveryBoyPhone:String?,
    var latitude:String?,
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
        var deliveryBoyName:String?,
        var deliveryBoyPhone:String?,


        var phone:String="",


){
    @PrimaryKey(autoGenerate = true)
    var uid:Int=0
}
data class DeliveryBoy(
        var deliveryBoyName:String?,
        var deliveryBoyPhone: String?
)


