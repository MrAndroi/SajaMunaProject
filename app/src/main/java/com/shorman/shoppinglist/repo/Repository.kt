package com.shorman.shoppinglist.repo

import androidx.lifecycle.LiveData
import com.shorman.shoppinglist.db.ShoppingDoa
import com.shorman.shoppinglist.db.ShoppingItem
import java.lang.Exception
import javax.inject.Inject


class Repository @Inject constructor (val doa:ShoppingDoa) {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)=
        doa.insertShoppingItem(shoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)=
        doa.deleteShoppingItem(shoppingItem)

    suspend fun updateItem(shoppingItem: ShoppingItem)=
        doa.updateItem(shoppingItem)

    fun getAllItems() =
        doa.getAllItems()

    suspend fun getTotalPrice():Float{
        if(doa.getTotalPrices() == null){
            return 0f
        }
        else{
            return doa.getTotalPrices()
        }
    }


    suspend fun deleteTable() = doa.deleteTable()

}