package com.shorman.shoppinglist.ui

import com.shorman.shoppinglist.db.ShoppingItem


interface AddDialogListener {
    fun onAddButtonClicked(item: ShoppingItem)
}