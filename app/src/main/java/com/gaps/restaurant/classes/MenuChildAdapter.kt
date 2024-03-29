package com.gaps.restaurant.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gaps.restaurant.R


class MenuChildAdapter(
    var list:List<Food>,var cart:ArrayList<Food>,var images:ArrayList<Image>,val adapterInterface: AdapterInterface,val context: Context
) :
    RecyclerView.Adapter<MenuChildAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuChildAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_menu, parent, false)
        return ViewHolder(itemView)
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var image:ImageView=view.findViewById(R.id.imageView5)
        var name:TextView=view.findViewById(R.id.textView2)
        var price:TextView=view.findViewById(R.id.tvFoodPrice)
        var count:TextView=view.findViewById(R.id.tvCount)
        var button: Button =view.findViewById(R.id.btAdd)
        var add: Button =view.findViewById(R.id.btIncrease)
        var sub: Button =view.findViewById(R.id.btDecrease)


    }
    fun notify(name:String){
        var index=0
        for( i in cart){
            if(i.name==name){
                notifyItemChanged(index)
                break
            }
            index++
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item=list[position]

        holder.name.text=item.name
        holder.price.text= item.price.toString()

        holder.image.setImageBitmap(images.find {
            it.name==item.name
        }?.image)

        var c=cart.find {
            it.name ==item.name
        }
        fun check(){
            if (c != null) {
                if(c.quantity > 0){
                    holder.button.visibility=View.GONE
                    holder.count.visibility=View.VISIBLE
                    holder.add.visibility=View.VISIBLE
                    holder.sub.visibility=View.VISIBLE
                }
                else{
                    holder.button.visibility=View.VISIBLE
                    holder.count.visibility=View.GONE
                    holder.add.visibility=View.GONE
                    holder.sub.visibility=View.GONE
                }
            }

        }
        check()
        if (c != null) {
            holder.count.text=c.quantity.toString()
        }

        holder.button.setOnClickListener {
            if (c != null) {
                adapterInterface.addToCartFromMenu(c)
                c.quantity++
                cart.find{
                    it.name==item.name
                }?.quantity=c.quantity

                notifyItemChanged(position)

            }


            it.visibility = View.GONE


        }
        holder.add.setOnClickListener {
            if (c != null) {
                adapterInterface.addToCartFromMenu(c)
                c.quantity++
                cart.find{
                    it.name==item.name
                }?.quantity=c.quantity
                notifyItemChanged(position)


            }


        }
        holder.sub.setOnClickListener {
            if (c != null) {
                adapterInterface.removeFromCartFromMenu(c)
                c.quantity--
                cart.find{
                    it.name==item.name
                }?.quantity=c.quantity

                notifyItemChanged(position)
            }
        }












        /*if(holder.count.text == "0")
            holder.button.visibility=View.VISIBLE



        else
            holder.button.visibility=View.GONE
        holder.image.setImageBitmap(images.find {
            it.name==item.name
        }!!.image)
        holder.button.setOnClickListener{
            adapterInterface.addToCart(item)
            cart.find {
                it.name ==item.name
            }?.quantity= item.quantity +1
            count(holder,item)
            buttonCheck(holder)

            Log.d("bt", holder.count.text.toString())
        }

        holder.add.setOnClickListener{
            adapterInterface.addToCart(item)
            cart.find {
                it.name ==item.name
            }!!.quantity= cart.find {
                it.name ==item.name
            }!!.quantity.plus(1)
            count(holder,item)
            buttonCheck(holder)

        }
        holder.sub.setOnClickListener{
            adapterInterface.removeFromCart(item)
            cart.find {
                it.name ==item.name
            }!!.quantity=cart.find {
                it.name ==item.name
            }!!.quantity!! - 1
            count(holder,item)
            buttonCheck(holder)
        }*/

    }

    private fun buttonCheck(holder: MenuChildAdapter.ViewHolder) {
        if(holder.count.text == "0")
            holder.button.visibility=View.GONE



        else
            holder.button.visibility=View.VISIBLE

    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun count(holder: ViewHolder,item:Food){
        holder.count.text=cart.find {
            it.name ==item.name
        }?.quantity.toString()

    }
}