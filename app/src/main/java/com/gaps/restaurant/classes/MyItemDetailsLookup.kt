package com.gaps.restaurant.classes

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class  MyItemDetailsLookup(private val recyclerView: RecyclerView) :
ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as AddressBookAdapter.ViewHolder)
                    .getItemDetails()
        }
        return null
    }
}
