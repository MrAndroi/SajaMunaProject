package com.shorman.shoppinglist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.models.User
import kotlinx.android.synthetic.main.search_list_item.view.*

class UserListAdapter() :RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {


    inner class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_list_item,parent,false))
    }

    val diffUtil = object : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val user = differ.currentList[position]

        holder.itemView.apply {
            tvUserName.text = user.name
            tvNickName.text = user.search

            setOnClickListener {
                onItemClickListener?.let { it(user) }
            }
        }
    }

    private var onItemClickListener:( (User) -> Unit )? = null

    fun onItemClick (listener:(User)->Unit){
        onItemClickListener = listener

    }
}