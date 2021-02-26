package com.example.restaurant.classes

interface AdapterInterface {

    fun addToCart(item:Food)
    fun removeFromCart(item:Food)
    fun removeAddress(item:Int)
    abstract fun selected(value: Int)

}