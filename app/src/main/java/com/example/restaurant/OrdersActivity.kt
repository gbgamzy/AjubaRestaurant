package com.example.restaurant

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant.api.Network
import com.example.restaurant.classes.AdapterInterface
import com.example.restaurant.classes.Food
import com.example.restaurant.classes.Order
import com.example.restaurant.classes.OrderAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_orders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersActivity : AppCompatActivity(), AdapterInterface {

    @Inject lateinit var api:Network
    var list:ArrayList<Order> =ArrayList()
    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var adapter:OrderAdapter
    var phone=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // calling the action bar
        // calling the action bar
        val actionBar: ActionBar? = supportActionBar

        // showing the back button in action bar

        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title="Orders"
        //setTheme(R.style.Theme_Orders)

        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        refresh()
        phone= pref.getString("phone","").toString()
        rvOrders.layoutManager=LinearLayoutManager(this)
        adapter= OrderAdapter(list,this,this)

        rvOrders.adapter=adapter



    }

    fun refresh(){
        CoroutineScope(Dispatchers.Main).launch {
            try{
                val p=api.getOrders(phone).body()
                list.clear()
                Log.d("vmList",p.toString())
                var sortedList=p?.sortedByDescending { it.date }

                if (sortedList != null) {
                    sortedList.forEach {
                        if(it.status!="D")
                            list.add(it)
                    }
                }

                adapter.notifyDataSetChanged()

            }
            catch(err:Exception){
                Log.d("vmOrderHistory",err.toString())

            }
        }
    }

    override fun addToCart(item: Food) {
        TODO("Not yet implemented")
    }

    override fun removeFromCart(item: Food) {
        TODO("Not yet implemented")
    }

    override fun removeAddress(item: Int) {
        TODO("Not yet implemented")
    }

    override fun selected(value: Int) {
        TODO("Not yet implemented")
    }
}