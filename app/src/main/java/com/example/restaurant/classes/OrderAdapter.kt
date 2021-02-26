@file:Suppress("DEPRECATION")

package com.example.restaurant.classes

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.MapsActivityOrders
import com.example.restaurant.R
import java.io.File
import java.text.SimpleDateFormat

class OrderAdapter(
    var list:ArrayList<Order>,var context: Context,
    var ad:AdapterInterface) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>(){
    val formatter= SimpleDateFormat("dd MM yyyy HH.mm")

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var title: TextView =view.findViewById(R.id.tvOrderTitle)
        var price: TextView =view.findViewById(R.id.tvOrderDate)
        var image: ImageView =view.findViewById(R.id.ivOrder)
        val layout:LinearLayout=view.findViewById(R.id.llOrderTrack)
        val layout1:LinearLayout=view.findViewById(R.id.llOrderHead)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_orders, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(list[position].status=="D"){
            return
        }
        var s=""
        list[position].contents.forEach {
            s+=it.name+" "+" x"+" ${it.quantity}"+", "
        }
        val image=findImage(list[position].contents[0].name)
        if (image != null) {
            holder.image.setImageBitmap(image.image)
        }
        holder.title.text=s
        val date=formatter.parse(list[position].date)
        val dateString=date.date.toString()+"/"+date.month.toString()

        holder.price.text= dateString
        if(list[position].status=="B"){
            holder.layout.visibility=View.VISIBLE
        }
        else{
            holder.layout.visibility=View.GONE
        }

        holder.layout1.setOnClickListener {
            if(list[position].status=="B" && list[position].deliveryBoy!=null){
                val intent = Intent(context,MapsActivityOrders::class.java)
                intent.putExtra("deliveryBoy", list[position].deliveryBoy?.phone)
                startActivity(context,intent,null)
            }
        }



    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun findImage(name:String): Image? {
        var image= BitmapFactory.decodeFile(context.getExternalFilesDir(null).toString() + File.separator + name + ".jpg")
        var img=Image(name = name,image = image)
        return img
    }
}