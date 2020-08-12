package com.shorman.shoppinglist.models

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import com.shorman.shoppinglist.db.ShoppingItem

@Keep
@IgnoreExtraProperties
data class User (
    val name:String?="",
    val uid:String?="",
    val search:String? ="",
    val token:String?="",
    val shoppingList: ArrayList<Sender>? = ArrayList()
)
