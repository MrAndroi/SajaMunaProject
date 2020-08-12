package com.shorman.shoppinglist.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface ShoppingDoa {

    @Insert(onConflict = REPLACE)
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_items")
    fun getAllItems() :LiveData<List<ShoppingItem>>

    @Update
    suspend fun updateItem(shoppingItem: ShoppingItem)

   @Query("SELECT SUM(totalPrice) FROM shopping_items")
   suspend fun getTotalPrices():Float

    @Query("DELETE FROM shopping_items")
    suspend fun deleteTable()


}