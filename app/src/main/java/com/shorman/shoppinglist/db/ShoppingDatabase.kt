package com.shorman.shoppinglist.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ShoppingItem::class],version = 7)
abstract class ShoppingDatabase:RoomDatabase() {

    abstract fun getShoppingDoa():ShoppingDoa

}