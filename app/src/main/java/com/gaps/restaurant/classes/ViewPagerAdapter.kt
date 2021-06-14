package com.gaps.restaurant.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaps.restaurant.R

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
        var list:ArrayList<Food> = ArrayList<Food>()
        menu[position].list.forEach {
            if(it.available==1){
                list.add(it)
            }
        }




        val adapter: MenuChildAdapter = MenuChildAdapter(list ,cart,images,i,context)
        holder.rv.layoutManager=LinearLayoutManager(context)
        holder.rv.itemAnimator=DefaultItemAnimator()
        holder.rv.adapter=adapter
        adapter.notifyDataSetChanged()



    }
    fun notify(name:String,category:String){
        var index=0
        menu.forEach {
            if(it.category=="All" || it.category=="category"){

            }
            index++
        }

    }



    override fun getItemCount(): Int {
        return menu.size
    }
}