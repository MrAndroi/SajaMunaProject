package com.shorman.shoppinglist.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.adapters.NotificationAdapter
import com.shorman.shoppinglist.models.Sender
import com.shorman.shoppinglist.models.User
import com.shorman.shoppinglist.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.notifications_fragment.*

@AndroidEntryPoint
class NotificationFragment:Fragment(R.layout.notifications_fragment) {

    val viewModel:MainViewModel by viewModels()
    lateinit var adapter:NotificationAdapter
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var list = ArrayList<Sender>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter(viewModel)
        rvNotification.adapter = adapter

        viewModel.navigateToLoaclFragment.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigate(R.id.action_notificationFragment_to_loacalListFragment)
                viewModel.doneNavigatingToLoaclFragment()
            }
        })

        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId.toString())
        firebaseDatabase.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                list = user?.shoppingList!!
                list.reverse()
                adapter.differ.submitList(list)
                if(progressBar2 != null) {
                    progressBar2.visibility = View.GONE
                }
                if(list.size.equals(0)){
                    if(tvNoNotifications != null) {
                        tvNoNotifications.visibility = View.VISIBLE
                    }
                }
            }

        })

        btnClearAll.setOnClickListener {
            val senderRef = FirebaseDatabase.getInstance().reference.child("users")
                .child(userId.toString()).child("shoppingList")
            senderRef.removeValue()
            Snackbar.make(requireView(),getString(R.string.cleared),Snackbar.LENGTH_SHORT).show()
        }

    }

}