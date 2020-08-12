package com.shorman.shoppinglist.models

import com.shorman.shoppinglist.db.ShoppingItem

data class Sender (
    val senderName:String="",
    val id:String="",
    val sendTime:Long = System.currentTimeMillis(),
    val shoppingList:List<ShoppingItem> = emptyList()
)