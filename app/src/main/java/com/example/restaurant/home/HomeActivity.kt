package com.example.restaurant.home



import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant.MapsActivity
import com.example.restaurant.OrdersActivity
import com.example.restaurant.R
import com.example.restaurant.auth.LoginActivity
import com.example.restaurant.classes.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.PI
import java.lang.StrictMath.PI
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeActivity  : AppCompatActivity(),AdapterInterface {

    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var viewModel:HomeActivityViewModel
    var lis:ArrayList<FoodMenu> = ArrayList<FoodMenu>()
    var img:ArrayList<Image> = ArrayList<Image>()
    var address:ArrayList<Address> = ArrayList<Address>()
    var cart:ArrayList<Food> = ArrayList<Food>()
    var filteredCart:ArrayList<Food> = ArrayList<Food>()
    var price:Double=0.0
    private val RECORD_REQUEST_CODE = 101
    var deliveryPrice:Double= 0.0
    var deliverable:Boolean=false
    var addressIndex:Int ?=null
    var admin:Admin ?= null
    lateinit var adAdapter:AddressBookAdapter
    val formatter= SimpleDateFormat("dd MM yyyy HH.mm")
    var minD=0.0
    var minN=0


    override fun onResume(){
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getAddress()
        }
        getTotal()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        viewModel=ViewModelProvider(this).get(HomeActivityViewModel::class.java)

        val adpater: ViewPagerAdapter = ViewPagerAdapter(lis, img, cart, this, this)
        val fcadapter: MenuChildAdapter = MenuChildAdapter(filteredCart, filteredCart, img, this)
        adAdapter= AddressBookAdapter(address, this)
        rvAddressBook.layoutManager=LinearLayoutManager(this)
        rvAddressBook.itemAnimator=DefaultItemAnimator()
        rvAddressBook.adapter=adAdapter
        rvCart.layoutManager=LinearLayoutManager(this)
        rvCart.itemAnimator= DefaultItemAnimator()
        rvCart.adapter=fcadapter
        vpMenu.adapter = adpater
        tracker = SelectionTracker.Builder<Long>(
                "mySelection",
                rvAddressBook,
                StableIdKeyProvider(rvAddressBook),
                MyItemDetailsLookup(rvAddressBook),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectSingleAnything()
        ).build()

        adAdapter.tracker = tracker


        textViewAddAddress.setOnClickListener{
            val intent =Intent(this, MapsActivity::class.java)
            startActivity(intent)



        }

        val addressObserver = Observer<ArrayList<Address>> { it ->
            // Update the UI, in this case, a TextView.
            address.clear()
            address.addAll(it)
            adAdapter.notifyDataSetChanged()




        }
        strHome.setOnRefreshListener {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.reloadMenu()
                strHome.isRefreshing=false

            }
        }
        getTotal()
        btPhone.setOnClickListener{
            val p=admin?.phone


            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + p)
            startActivity(dialIntent)
        }
        btOrderHistory.setOnClickListener {
            val intent= Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        btLogOut.setOnClickListener{

            val dialog=AlertDialog.Builder(this)

            dialog.setTitle("Log Out ?")
            dialog.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                logout()
            }
            dialog.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

            }
            dialog.create().show()


        }


            val menuObserver = Observer<ArrayList<FoodMenu>> { it ->
                // Update the UI, in this case, a TextView.
                Log.d("vmList", it.toString())
                lis.clear()
                lis.addAll(it)
                adpater.notifyDataSetChanged()




            }
            val imageObserver = Observer<ArrayList<Image>> { it ->
                // Update the UI, in this case, a TextView.
                img.clear()
                img.addAll(it)
                adpater.notifyDataSetChanged()


            }
            val cartObserver = Observer<ArrayList<Food>> { it ->
                // Update the UI, in this case, a TextView.
                cart.clear()
                cart.addAll(it)
                adpater.notifyDataSetChanged()


            }
            val filteredCartObserver = Observer<ArrayList<Food>> { it ->
                // Update the UI, in this case, a TextView.
                filteredCart.clear()
                filteredCart.addAll(it)
                Log.d("size", filteredCart.toString())
                fcadapter.notifyDataSetChanged()
                price= 0.0
                filteredCart.forEach {
                    price+=it.quantity*it.price
                }
                




            }
            viewModel.admin.observe(this, {
                admin = it
            })
            viewModel.cart.observe(this, cartObserver)
            viewModel.filteredCart.observe(this, filteredCartObserver)
            viewModel.address.observe(this, addressObserver)
            viewModel.images1.observe(this, imageObserver)

            viewModel.foodMenu.observe(this, menuObserver)




            btPlaceOrder.setOnClickListener{
                if(deliveryPrice==price){
                    DNASnackBar.show(this, "The cart is empty")
                }
                if(!deliverable){
                    DNASnackBar.show(this, "Long press an address within the delivery range")
                    rvAddressBook.requestFocus()
                }
                else{

                    val dialog=AlertDialog.Builder(this)

                    dialog.setTitle("Place order?")
                    dialog.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                        var content:ArrayList<Content> = ArrayList()
                        filteredCart.forEach{
                            val p:Content = Content(it.name, it.quantity)
                            content.add(p)
                        }
                        var date: Date = Date()
                        val i=formatter.format(date)
                        val order = Order(content, price.roundToInt(), i, null, address[addressIndex!!], null)
                        Log.d("vmErrorPlaceOrder", order.toString())
                        try{
                            CoroutineScope(Dispatchers.Main).launch {
                                val msg = pref.getString("phone", "")?.let { it1 -> viewModel.placeOrder(it1, order) }
                                if (msg == "SUCCESS") {
                                    DNASnackBar.show(this@HomeActivity, "Order Placed")
                                    viewModel.filteredCart.value=ArrayList()

                                    viewModel.clearCart()

                                } else if (msg == "ERROR") {
                                    DNASnackBar.show(this@HomeActivity, "There seems to be some problem")
                                }
                                Log.d("msg", msg!!)
                            }
                        }
                        catch (err: Exception){
                            Log.d("vmErrorPlaceOrder", err.toString())
                            DNASnackBar.show(this@HomeActivity, "There seems to be some problem")
                        }
                    }
                    dialog.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->

                    }
                    dialog.create().show()



                }

            }



        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getMenu()
            //viewModel.getCart()

            viewModel.reloadMenu()

        }



        TabLayoutMediator(tbMenu, vpMenu){ tab, position->
            tab.text=lis[position].category
        }.attach()


        vpMenu.beginFakeDrag()
        vpMenu.fakeDragBy(-7f)
        vpMenu.endFakeDrag()

    }

    override fun addToCart(item: Food) {
        item.quantity++

        CoroutineScope(Dispatchers.Main).launch { 

            viewModel.addToCart(item)
            viewModel.getCart()
            getTotal()
        }


    }

    override fun removeFromCart(item: Food) {
        item.quantity--
        CoroutineScope(Dispatchers.Main).launch { viewModel.removeFromCart(item)
            viewModel.getCart()}
        getTotal()
    }

    override fun removeAddress(item: Int) {
        CoroutineScope(Dispatchers.Main).launch { viewModel.removeAddress(item)
            viewModel.getCart()}
    }

    override fun selected(value: Int) {
        addressIndex=value
        getTotal()
        Log.d("Selection0", value.toString())

    }
    fun getTotal(){
        var distance:Double= 0.0
        price=0.0
        deliverable=false
        deliveryPrice=0.0
        minD=0.0
        minN=0
        Log.d("Admin", admin?.address.toString())
        if(adAdapter.tracker?.selection?.isEmpty == true){



        }

        else {

            admin?.address?.latitude?.let {
                admin?.address?.longitude?.let { it1 ->
                    distance=getDistance(address[addressIndex!!].latitude, address[addressIndex!!].longitude,
                            it, it1)/1000
                }

            }
            Log.d("DistanceRestaurant", (admin?.prices?.dist1!!.compareTo(distance)).toString()+"xcx"+
                    (distance>admin?.prices?.dist1!!).toString())


            if(distance<admin?.prices?.dist1!!){

                minD= admin?.prices?.dist1!! -distance

                minN=1
                Log.d("Stats",minD.toString()+minN.toString())
            }
            if(distance<admin?.prices?.dist2!!){
                if((admin?.prices?.dist2!!-distance)<minD || minD==0.0){
                    minD=admin?.prices?.dist2!!-distance
                    minN=2

                Log.d("Stats2",minD.toString()+minN.toString())}
            }
            if(distance<admin?.prices?.dist3!! ){
                if((admin?.prices?.dist3!!-distance)<minD || minD==0.0 ){
                    minD=admin?.prices?.dist3!!-distance
                    minN=3

                Log.d("Stats3",minD.toString()+minN.toString())}
            }


            if(minN==1){
                deliverable=true
                deliveryPrice=admin?.prices?.price1!!
            }
            if(minN==2){
                deliverable=true
                deliveryPrice=admin?.prices?.price2!!
            }
            if(minN==3){
                deliverable=true
                deliveryPrice=admin?.prices?.price3!!
            }
            if(minN==0){
                deliverable=false
                deliveryPrice=0.0

            }
            Log.d("MinN",distance.toString())

        }
        filteredCart.forEach {
            price+= it.quantity*it.price
        }



        implementTotal()



    }

    private fun implementTotal() {
        var p=(deliveryPrice+price)
        textView.text= "Delivery price        ₹ $deliveryPrice"
        textView9.text="Total price           ₹ $p"
    }

    fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        var x= FloatArray(1)
        val p=Location.distanceBetween(lat1,lng1,lat2,lng2,x)


        return x[0].toDouble()
    }
    fun logout(){
        edit.putBoolean("loggedIn", false)
        edit.apply()
        edit.commit()
        val intent:Intent=Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


}



