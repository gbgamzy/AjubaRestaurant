@file:Suppress("DEPRECATION")

package com.example.restaurant.classes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        var date: TextView =view.findViewById(R.id.tvOrderDate)
        var image: ImageView =view.findViewById(R.id.ivOrder)
        val layout:LinearLayout=view.findViewById(R.id.llOrderTrack)
        val layout1:LinearLayout=view.findViewById(R.id.llOrderHead)
        val price:TextView =view.findViewById(R.id.tvOrderPrice)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_orders, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        Log.d("ADAPTERLIST",list.toString())
        val item=list[position]
        val items= item.contents?.split(" x ")?.toTypedArray()
        items?.get(0)?.let { Log.d("image name", it) }
        holder.image.setImageBitmap(items?.get(0)?.let { findImage(it)?.image })
        holder.title.text=item.contents
        holder.price.text= item.price.toString()

        if(item.date!=""){
            val date = formatter.parse(item.date)
            val dateString = date.date.toString() + "/" + date.month.toString()


        holder.date.text= dateString}
        if(item.status=="B"){
            holder.layout.visibility=View.VISIBLE
        }
        else{
            holder.layout.visibility=View.GONE
        }

        holder.layout.setOnClickListener {



                val intent = Intent(context,MapsActivityOrders::class.java)
                intent.putExtra("DbPhone",item.deliveryBoy)


                startActivity(context,intent,null)

        }




    }

    override fun getItemCount(): Int {

        return list.size
    }
    fun findImage(name:String): Image? {
        var image: Bitmap?= BitmapFactory.decodeFile(context.getExternalFilesDir(null).toString() + File.separator + name + ".jpg")
        if (image != null) {
            Log.d("image size",image.byteCount.toString())
        }
        var img=Image(name = name,image = image)
        return img
    }
}