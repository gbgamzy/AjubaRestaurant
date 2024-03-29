package com.gaps.restaurant.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.gaps.restaurant.R

class AddressBookAdapter(
        var list:ArrayList<Address>,var context:Context,
var ad:AdapterInterface) :
        RecyclerView.Adapter<AddressBookAdapter.ViewHolder>(){
    var tracker: SelectionTracker<Long>? = null
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {


        var title: TextView =view.findViewById(R.id.tvAddressTitle)
        var address: TextView =view.findViewById(R.id.tvAddress)
        var delete: ImageButton =view.findViewById(R.id.btDelete)
        var layout: LinearLayout =view.findViewById(R.id.llAddress)


        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>() {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): Long? = itemId
                }
        fun bind(value: Int, isActivated: Boolean = false) {


            title.text=list[value].houseName
            address.text= list[value].streetAddress
            delete.setOnClickListener{
                ad.removeAddress(list[value].uid)
            }
            layout.setOnClickListener {

                    tracker?.select(value.toLong())
                    ad.selected(value)
                if(tracker?.hasSelection()==false){
                    ad.selected(-1)
                }

            }


            itemView.isActivated = isActivated

            if(!itemView.isActivated){
                ad.selected(-1)
            }

                ad.selected(value)


        }

    }
    init {
        setHasStableIds(true)
    }
    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressBookAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ticket_address_book, parent, false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        tracker?.let {
            holder.bind(position, it.isSelected(position.toLong()))
        }



    }

    override fun getItemCount(): Int {

            return list.size

    }
}
