package com.gaps.restaurant.classes

interface AdapterInterface {

    fun addToCart(item:Food)
    fun removeFromCart(item:Food)
    fun removeAddress(item:Int)
    fun selected(value: Int)
    fun addToCartFromMenu(c: Food)
    fun removeFromCartFromMenu(c: Food)


}