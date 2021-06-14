package com.gaps.restaurant.home

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.gaps.restaurant.api.Network
import com.gaps.restaurant.classes.*
import com.gaps.restaurant.db.MenuDAO
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
         Log.d("filteredCart",filteredCart.toString()+" "+filteredList.toString())




    }
    suspend fun reloadAdmin():Admin?{

        admin.value = api.getAdmin().body()
        return admin.value


    }

    suspend fun reloadMenu():Boolean{
        try{
            db.clearCart()
            db.clearMenu()
            val foodList: ArrayList<Food> = ArrayList()
            val list: List<FoodMenu> = api.getMenu().body()!!
            Log.d("vmvm", list.toString())

            val list1: ArrayList<FoodMenu> = ArrayList()


            val foods=api.getFood().body()
            if (foods != null) {
                foodList.addAll(foods)
            }
            reloadAdmin()

            Log.d("lists.",admin.value.toString())
            val arrayList:ArrayList<Food> = ArrayList()
            arrayList.addAll(foodList.sortedWith(compareBy { it.name }))

            var allFood = FoodMenu(
                category = "All",
                list =  arrayList
            )
            list1.add(allFood)
            list.forEach { menu->
                var fl:ArrayList<Food> = ArrayList()
                foodList.forEach {


                    if(it.category==menu.category){
                        fl.add(it)



                    }
                }
                Log.d("FoodMenuList",fl.toString())
                menu.list=fl

            }
            //list1.addAll(list)
            foodMenu.postValue(list1)
            list.forEach {


                db.addMenu(it)
                it.list.forEach { t ->

                    db.addToCart(t)

                }




            }
            getCart()


            if(!getImages(foodList))
                reloadImages(foodList)
            getAddress()
            return true



        }
        catch(err:Exception){
            Log.d("vmErrorReloadMenu",err.toString())
            return false

        }




    }
    suspend fun placeOrder(phone:String,order:Order):String?{
        try{
            var x=api.placeOrder(phone,order)
            return x.body()?.message


        }
        catch(err:Exception){
            Log.d("placerorder",err.toString())
            return Message("ERROR").message



        }

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

        foodMenu.postValue(list1)
        getImages(foodList)
        getAddress()

    }

    suspend fun removeFromCart(item: Food){
        Log.d("removeFromCart",item.toString())
        var count:Int=db.getItem(item.name)
        if(count!=null) {
            if (count > 0)
                item.quantity = count - 1
            db.addToCart(item)
        }
        else{
            db.addToCart(item)
        }
    }

    suspend fun addToCart(item: Food){
        Log.d("addtocart",item.toString())
        var count:Int=db.getItem(item.name)
        item.quantity=count + 1
        db.addToCart(item)

    }
    private suspend fun reloadImages(foodList: ArrayList<Food>) {
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
    suspend fun getImages(foodList: ArrayList<Food>):Boolean{
        var images=ArrayList<Image>()
        foodList.forEach {

            try{
                var image = BitmapFactory.decodeFile(
                    context.getExternalFilesDir(null).toString() + File.separator + it.name + ".jpg"
                )
                if(image==null)
                    return false
                var img=Image(name = it.name,image = image)
                images.add(img)
            }
            catch(err:Exception){
                return false
            }

        }
        images1.postValue(images)
        return true
    }
    suspend fun logout(phone:String){
        try{ api.login(phone, "aaa","name") }
        catch(err:Exception){


        }

    }
}