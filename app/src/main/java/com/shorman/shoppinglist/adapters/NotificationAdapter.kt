package com.shorman.shoppinglist.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.models.Sender
import com.shorman.shoppinglist.ui.MainViewModel
import kotlinx.android.synthetic.main.notification_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(val viewModel: MainViewModel):RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item,parent,false))

    }

    val diffCallback = object :DiffUtil.ItemCallback<Sender>(){
        override fun areItemsTheSame(oldItem: Sender, newItem: Sender): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sender, newItem: Sender): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val sender = differ.currentList[position]

        holder.itemView.apply {
            tvSenderName.text = sender.senderName
            tvSendTime.text = getDate(sender.sendTime,"dd/MM/yyyy hh:mm:ss")
            btnAddTodatabase.setOnClickListener {
                viewModel.deleteTable()
                for (i in sender.shoppingList){
                    viewModel.insertShoppingItem(i)
                }
                viewModel.navigateToLoacalFragment()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(time:Long, format:String):String{

        val formater = SimpleDateFormat(format)

        val calender = Calendar.getInstance()
        calender.setTimeInMillis(time)
        return formater.format(calender.time)

    }
}