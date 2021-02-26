package com.example.restaurant.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R

class ViewPagerAdapter(
        private var menu: ArrayList<FoodMenu>, private var images: ArrayList<Image>, private var cart: ArrayList<Food>,
        private val context: Context, private val i: AdapterInterface
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>(){


    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        var rv:RecyclerView=view.findViewById(R.id.rvFood)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_menu, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val adapter: MenuChildAdapter = MenuChildAdapter(menu[position].list ,cart,images,i)
        holder.rv.layoutManager=LinearLayoutManager(context)
        holder.rv.itemAnimator=DefaultItemAnimator()
        holder.rv.adapter=adapter
        adapter.notifyDataSetChanged()


    }



    override fun getItemCount(): Int {
        return menu.size
    }
}