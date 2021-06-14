package com.gaps.restaurant.home



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
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
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
import com.gaps.restaurant.MapsActivity
import com.gaps.restaurant.OrdersActivity
import com.gaps.restaurant.R
import com.gaps.restaurant.auth.LoginActivity
import com.gaps.restaurant.classes.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeActivity  : AppCompatActivity(),AdapterInterface {

    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var viewModel:HomeActivityViewModel
    var menu:ArrayList<FoodMenu> = ArrayList<FoodMenu>()
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
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    lateinit var adpater:ViewPagerAdapter
    lateinit var fcadapter:CartAdapter



    override fun onResume(){
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getAddress()
        }
        getTotal()

    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        viewModel=ViewModelProvider(this).get(HomeActivityViewModel::class.java)

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                HomeActivity.LOCATION_PERMISSION_REQUEST_CODE
            )





        }


        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val scrollViewHeight = nestedScrollView.height
                if (scrollViewHeight > 0) {
                    nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val lastView = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
                    val lastViewBottom = lastView.bottom + nestedScrollView.paddingBottom
                    val deltaScrollY = lastViewBottom - scrollViewHeight - nestedScrollView.scrollY
                    /* If you want to see the scroll animation, call this. */nestedScrollView.smoothScrollBy(
                        0,
                        deltaScrollY
                    )
                    /* If you don't want, call this. */nestedScrollView.scrollBy(0, deltaScrollY)
                }
            }
        })



        adpater = ViewPagerAdapter(menu, img, cart, this, this)
        fcadapter = CartAdapter(filteredCart, filteredCart, img, this,this)

        adAdapter= AddressBookAdapter(address, this,this)
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

        my_toolbar.setOnClickListener{
            nestedScrollView.fullScroll(View.FOCUS_DOWN)
        }
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

        nestedScrollView.isSmoothScrollingEnabled=true

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
                menu.clear()
                menu.addAll(it)
                adpater.notifyDataSetChanged()
                fcadapter.notifyDataSetChanged()




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
                //adpater.notifyDataSetChanged()
                fcadapter.notifyDataSetChanged()

            }
            val filteredCartObserver = Observer<ArrayList<Food>> { it ->
                // Update the UI, in this case, a TextView.
                filteredCart.clear()
                filteredCart.addAll(it)
                filteredCart.sortBy {
                    it.name
                }
                Log.d("size", filteredCart.toString())
                fcadapter.notifyDataSetChanged()

                




            }
            viewModel.admin.observe(this, {
                admin = it
                setMini(admin)
            })
            viewModel.cart.observe(this, cartObserver)
            viewModel.filteredCart.observe(this, filteredCartObserver)
            viewModel.address.observe(this, addressObserver)
            viewModel.images1.observe(this, imageObserver)

            viewModel.foodMenu.observe(this, menuObserver)




            btPlaceOrder.setOnClickListener{
                Log.d("prices", tracker?.selection.toString())

                if(price==0.0 ){
                    DNASnackBar.show(applicationContext, "The cart is empty")
                    return@setOnClickListener
                }
                if(!deliverable || tracker?.selection?.size()==0){
                    DNASnackBar.show(applicationContext, "Long press an address, to select a valid address")
                    selected(-1)
                    rvAddressBook.requestFocus()
                    return@setOnClickListener
                }
                else{

                    val dialog=AlertDialog.Builder(this)

                    dialog.setTitle("Place order?")
                    dialog.setPositiveButton("Yes"){ dialogInterface: DialogInterface, i: Int ->
                        var content = ""
                        var count=1
                        filteredCart.forEach{
                            if(count!=filteredCart.size)
                                content += it.name+" x "+it.quantity+", "
                            else
                                content += it.name+" x "+it.quantity
                            count++


                        }
                        var date: Date = Date()
                        val i=formatter.format(date)
                        val order = Order(pref.getString("name","name"),0,content, (deliveryPrice+price).roundToInt(), i, null,0,
                            address[addressIndex!!].houseName,address[addressIndex!!].streetAddress,
                            address[addressIndex!!].latitude,address[addressIndex!!].longitude,"",
                            "")
                        Log.d("vmErrorPlaceOrder", order.toString())
                        try{
                            CoroutineScope(Dispatchers.Main).launch {
                                try{
                                    if (viewModel.reloadAdmin()?.registrationToken != "") {
                                        val msg = pref.getString("phone", "")
                                            ?.let { it1 -> viewModel.placeOrder(it1, order) }
                                        if (msg == "SUCCESS") {
                                            DNASnackBar.show(applicationContext, "Order Placed")
                                            viewModel.filteredCart.value = ArrayList()

                                            progressBar.visibility = View.VISIBLE
                                            if (viewModel.reloadMenu())
                                                progressBar.visibility = View.GONE
                                            fcadapter.notifyDataSetChanged()
                                            getTotal()


                                        } else if (msg == "ERROR") {
                                            DNASnackBar.show(
                                                this@HomeActivity,
                                                "There seems to be some problem"
                                            )
                                        }
                                        Log.d("msg", msg!!)
                                    } else {
                                        DNASnackBar.show(
                                            this@HomeActivity,
                                            "Sorry we're not accepting orders right now!"
                                        )
                                    }
                                }
                                catch(err:Exception){
                                    DNASnackBar.show(this@HomeActivity,"Order declined. Try again.")
                                }
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
            progressBar.visibility=View.VISIBLE
            //viewModel.getMenu()

            viewModel.getCart()

            if(viewModel.reloadMenu()){
                progressBar.visibility=View.GONE

            }




        }

        var bottomSheet = BottomSheetBehavior.from(nestedScrollView)

        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED)
        bottomSheet.setState(BottomSheetBehavior.STATE_SETTLING)




        TabLayoutMediator(tbMenu, vpMenu){ tab, position->
            tab.text=menu[position].category
        }.attach()


        vpMenu.beginFakeDrag()
        vpMenu.fakeDragBy(-7f)
        vpMenu.endFakeDrag()



    }

    private fun setMini(admin: Admin?) {
        if(admin!=null) {

            textView11.text =
                "* Delivery charges for orders below ₹${admin.minimumDistance} is ₹${admin.minimumPrice}"
        }
    }

    override fun addToCart(item: Food) {
        item.quantity++

        CoroutineScope(Dispatchers.Main).launch {

            viewModel.addToCart(item)
            viewModel.getCart()
            cart.find {
                it.name==item.name
            }?.quantity=item.quantity
            adpater.notifyDataSetChanged()
            getTotal()
        }


    }

    override fun removeFromCart(item: Food) {
        //item.quantity--
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.removeFromCart(item)
            viewModel.getCart()
            cart.find {
                it.name==item.name
            }?.quantity=item.quantity
            adpater.notifyDataSetChanged()
            getTotal()
        }



    }

    override fun removeAddress(item: Int) {
        CoroutineScope(Dispatchers.Main).launch { viewModel.removeAddress(item)
            }
    }

    override fun selected(value: Int) {
        addressIndex=value

            getTotal()
            Log.d("Selection0", value.toString())

    }

    override fun addToCartFromMenu(item: Food) {
        //item.quantity++

        CoroutineScope(Dispatchers.Main).launch {



            viewModel.addToCart(item)
            viewModel.getCart()
            fcadapter.notifyDataSetChanged()

            getTotal()
        }
    }

    override fun removeFromCartFromMenu(item: Food) {
        //item.quantity--

        CoroutineScope(Dispatchers.Main).launch {

            viewModel.removeFromCart(item)
            viewModel.getCart()
            fcadapter.notifyDataSetChanged()

            getTotal()
        }
    }

    fun getTotal(){
        if(addressIndex==-1){
            deliveryPrice=0.0
            implementTotal()
            return
        }
        var distance:Double= 0.0
        price=0.0
        deliverable=false
        deliveryPrice=0.0
        minD=0.0
        minN=0
        filteredCart.forEach {
            price+= it.quantity*it.price
        }

//        if(adAdapter.tracker?.selection?.isEmpty != true && price< admin?.minimumDistance!!){
//
//            deliverable=true
//            deliveryPrice= admin?.minimumPrice!!
//
//        }

        if(adAdapter.tracker?.selection?.isEmpty != true ) {

            admin?.latitude?.let {
                admin?.longitude?.let { it1 ->
                    distance=getDistance(address[addressIndex!!].latitude, address[addressIndex!!].longitude,
                            it, it1)/1000
                }

            }
            Log.d("DistanceRestaurant", (admin?.dist1!!.compareTo(distance)).toString()+"xcx"+
                    (distance>admin?.dist1!!).toString())


            if(distance<admin?.dist1!!){

                minD= admin?.dist1!! -distance

                minN=1
                Log.d("Stats",minD.toString()+minN.toString())
            }
            if(distance<admin?.dist2!!){
                if((admin?.dist2!!-distance)<minD || minD==0.0){
                    minD=admin?.dist2!!-distance
                    minN=2

                Log.d("Stats2",minD.toString()+minN.toString())}
            }
            if(distance<admin?.dist3!! ){
                if((admin?.dist3!!-distance)<minD || minD==0.0 ){
                    minD=admin?.dist3!!-distance
                    minN=3

                Log.d("Stats3",minD.toString()+minN.toString())}
            }


            if(minN==1){
                deliverable=true
                deliveryPrice=admin?.price1!!
            }
            if(minN==2){
                deliverable=true
                deliveryPrice=admin?.price2!!
            }
            if(minN==3){
                deliverable=true
                deliveryPrice=admin?.price3!!
            }
            if(minN==0){
                deliverable=false
                deliveryPrice=0.0

            }
            if(deliverable&&price< admin?.minimumDistance!!){
                deliveryPrice= admin?.minimumPrice!!
            }
            Log.d("MinN",distance.toString())

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
        try{
            CoroutineScope(IO).launch {
                pref.getString("phone", "")?.let { viewModel.logout(it) }
            }
        }
        catch(err:Exception){


        }

        startActivity(intent)
        finish()

    }


}



