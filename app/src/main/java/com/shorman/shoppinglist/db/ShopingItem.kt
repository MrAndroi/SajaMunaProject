package com.shorman.shoppinglist.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem (
    var quantity:Int=0,
    var name:String="",
    var price:Float=0f,
    var totalPrice:Float? = quantity * price

){
    @PrimaryKey(autoGenerate = true)
    var id :Int? = null
}