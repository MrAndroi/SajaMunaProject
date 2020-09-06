package com.shorman.shoppinglist.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shorman.shoppinglist.db.ShoppingItem
import com.shorman.shoppinglist.repo.Repository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val repo:Repository):ViewModel() {

    val totalPrice = repo.getTotalPrice()
    val navigateToLoaclFragment = MutableLiveData<Boolean>()

    fun insertShoppingItem(shoppingItem: ShoppingItem)=viewModelScope.launch {
        repo.insertShoppingItem(shoppingItem)
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem)=viewModelScope.launch {
        repo.deleteShoppingItem(shoppingItem)
    }

    fun updateItem(shoppingItem: ShoppingItem)=viewModelScope.launch {
        repo.updateItem(shoppingItem)
    }

    fun getShoppingItems() = repo.getAllItems()

   fun deleteTable() = viewModelScope.launch {
       repo.deleteTable()
   }

    fun navigateToLoacalFragment(){
        navigateToLoaclFragment.value = true
    }

    fun doneNavigatingToLoaclFragment(){
        navigateToLoaclFragment.value = false
    }

}