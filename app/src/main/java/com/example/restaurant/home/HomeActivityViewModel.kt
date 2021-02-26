package com.example.restaurant.home

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restaurant.api.Network
import com.example.restaurant.classes.*
import com.example.restaurant.db.MenuDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class HomeActivityViewModel  @ViewModelInject constructor(private val api: Network, private val db: MenuDAO, application: Application): AndroidViewModel(application){


    private val context = getApplication<Application>().applicationContext
    val admin: MutableLiveData<Admin> = MutableLiveData()

    val images1:MutableLiveData<ArrayList<Image>> = MutableLiveData()
    val foodMenu:MutableLiveData<ArrayList<FoodMenu>> = MutableLiveData()
    val cart:MutableLiveData<ArrayList<Food>> = MutableLiveData()
    val filteredCart:MutableLiveData<ArrayList<Food>> = MutableLiveData()
    val address:MutableLiveData<ArrayList<Address>> = MutableLiveData()




    suspend fun removeAddress(uid:Int){
        db.deleteAddress(uid)
        getAddress()
    }

    suspend fun getAddress(){
        val list:ArrayList<Address> = ArrayList()
        list.addAll(db.getAddress())
        address.value=list
    }


    
     suspend fun getCart(){

        var list:ArrayList<Food> = ArrayList()
        var filteredList:ArrayList<Food> = ArrayList()

        list.addAll(db.getCart())
         cart.postValue(list)
         list.forEach {
             if (it.quantity>0){
                 filteredList.add(it)
             }
         }
         filteredCart.value=filteredList
         Log.d("vm",filteredCart.toString()+" "+filteredList.toString())




    }

    suspend fun reloadMenu(){
        try{
            db.clearCart()
            db.clearMenu()
            val foodList: ArrayList<Food> = ArrayList()
            val list: List<FoodMenu> = api.getMenu().body()!!
            Log.d("vmvm", list.toString())

            val list1: ArrayList<FoodMenu> = ArrayList()

            list.forEach {


                db.addMenu(it)
                it.list.forEach { t ->

                    db.addToCart(t)

                }
                foodList.addAll(it.list.sortedWith(compareBy{it.name}))



            }
            admin.value=api.getAdmin().body()
            Log.d("lists.",admin.value.toString())
            var allFood = FoodMenu(
                category = "All",
                list = foodList.sortedWith(compareBy { it.name }).toList()
            )
            list1.add(allFood)
            list1.addAll(list)
            foodMenu.postValue(list1)
            getCart()



            reloadImages(foodList)
            getAddress()

        }
        catch(err:Exception){
            Log.d("vmErrorReloadMenu",err.toString())
        }




    }
    suspend fun placeOrder(phone:String,order:Order):String?{
        return api.placeOrder(phone,order).body()?.message

    }
    suspend fun clearCart(){
        db.clearCart()
        getCart()
    }
    suspend fun getMenu(){
        val foodList:ArrayList<Food> = ArrayList()

        val list:List<FoodMenu>?=db.getMenu()
        val list1:ArrayList<FoodMenu> = ArrayList()

        list?.forEach {


            foodList.addAll(it.list)
        }
        var allFood=FoodMenu(category = "All", list = foodList.sortedWith(compareBy { it.name }).toList())
        list1.add(allFood)
        db.getMenu()?.let { list1.addAll(it) }
        foodMenu.postValue(list1)
        getImages(foodList)
        getAddress()


    }

    suspend fun removeFromCart(item: Food){
        var count:Int=db.getItem(item.name)
        if(item.quantity!! > 0)
        item.quantity=count - 1
        db.addToCart(item)
    }
    suspend fun addToCart(item: Food){
        var count:Int=db.getItem(item.name)
        item.quantity=count + 1
        db.addToCart(item)

    }
    suspend fun reloadImages(foodList: ArrayList<Food>) {
        try{
            var images = ArrayList<Image>()
            foodList.forEach {


                var body = api.getImage(it.image).body()
                val futureStudioIconFile: File =
                    File(context.getExternalFilesDir(null), File.separator + it.name + ".jpg")

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                val fileReader = ByteArray(4096)
                val fileSize = body!!.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    var read = inputStream.read(fileReader)


                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read

                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()


            }
            getImages(foodList)
        }
        catch(err:Exception){
            Log.d("vmErrorReloadImages",err.toString())
        }


    }
    suspend fun getImages(foodList: ArrayList<Food>){
        var images=ArrayList<Image>()
        foodList.forEach {

            var image= BitmapFactory.decodeFile(context.getExternalFilesDir(null).toString() + File.separator + it.name + ".jpg")
            var img=Image(name = it.name,image = image)
            images.add(img)
        }
        images1.postValue(images)
    }
}