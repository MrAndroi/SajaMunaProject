package com.shorman.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.db.ShoppingItem
import com.shorman.shoppinglist.ui.MainViewModel
import kotlinx.android.synthetic.main.shopping_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingAdapter(val viewModel:MainViewModel,
                      private val onClickListener:() -> Unit,
                      private val onLongClickListener:(shoppingItem:ShoppingItem,item:View) -> Boolean)
    :RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder>() {


    inner class ShoppingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {}

    private val diffUtil = object:DiffUtil.ItemCallback<ShoppingItem>(){
        override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
            return oldItem==newItem
        }

    }

    val differ =AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        return ShoppingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.shopping_item,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val shoppingItem = differ.currentList[position]

        holder.itemView.apply {
            tvName.text = shoppingItem.name
            tvAmount.text = shoppingItem.quantity.toString()

            ivMinus.setOnClickListener {
                if(shoppingItem.quantity == 0){
                    Snackbar.make(this,context.getString(R.string.u_cant_add_less_than_1),Snackbar.LENGTH_SHORT).show()
                }else {
                    shoppingItem.quantity--
                    shoppingItem.totalPrice = shoppingItem.price * shoppingItem.quantity
                    viewModel.updateItem(shoppingItem)
                }

            }

            ivPlus.setOnClickListener {
                    shoppingItem.quantity++
                    shoppingItem.totalPrice = shoppingItem.price*shoppingItem.quantity
                    viewModel.updateItem(shoppingItem)
                }


            tvPrice.text = shoppingItem.price.toString()+" $"

            setOnClickListener { onClickListener() }
            setOnLongClickListener { onLongClickListener(shoppingItem,it) }

        }
    }


}