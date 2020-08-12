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

    val isIncreaseClicked = MutableLiveData<Boolean>()
    val isDecreamnetClicked = MutableLiveData<Boolean>()
    val isDeleteClicked= MutableLiveData<Boolean>()
    val isAddClicked = MutableLiveData<Boolean>()
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

    suspend fun getTotalPrice() : Float {
        return repo.getTotalPrice()
    }

    fun increaseClicked(){
        isIncreaseClicked.value = true
        isDecreamnetClicked.value=false
    }

    fun decraseClicked(){
        isDecreamnetClicked.value = true
        isIncreaseClicked.value=true
    }

    fun deleteClicked(){
        isDeleteClicked.value = true
    }

   fun addClicked(){
       isAddClicked.value = true
   }

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