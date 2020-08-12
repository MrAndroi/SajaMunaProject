package com.shorman.shoppinglist.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.adapters.ShoppingAdapter
import com.shorman.shoppinglist.db.ShoppingItem
import com.shorman.shoppinglist.models.User
import com.shorman.shoppinglist.ui.AddDialogListener
import com.shorman.shoppinglist.ui.AddShoppingItemDialog
import com.shorman.shoppinglist.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_loacal_list.*
import kotlinx.android.synthetic.main.notifications_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoacalListFragment :Fragment(R.layout.fragment_loacal_list) {


    val viewModel:MainViewModel by viewModels()
    lateinit var shoppingAdapter: ShoppingAdapter
    lateinit var connMgr:ConnectivityManager
    lateinit var networkInfo:NetworkInfo

    override fun onResume() {
        super.onResume()
        tvUserName.text = FirebaseAuth.getInstance().currentUser?.displayName
    }

    override fun onPause() {
        super.onPause()
        tvUserName.text = ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        shoppingAdapter = ShoppingAdapter(viewModel)

        connMgr =  getActivity()
            ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkInfo = connMgr.activeNetworkInfo!!


        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = shoppingAdapter.differ.currentList[position]
                viewModel.deleteShoppingItem(item)
                viewModel.deleteClicked()
            }

        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvShoppingItems)

        viewModel.getShoppingItems().observe(viewLifecycleOwner, Observer {
            shoppingAdapter.differ.submitList(it)
            rvShoppingItems.adapter = shoppingAdapter

        })

        fabSend.setOnClickListener {
            if (networkInfo.isConnected()) {
                findNavController().navigate(R.id.action_loacalListFragment_to_searchListActivity)
            } else {
                Snackbar.make(requireView(),getString(R.string.u_cant_use_this_feature),Snackbar.LENGTH_SHORT).show()
            }
        }

        fab.setOnClickListener {
            AddShoppingItemDialog(
                requireActivity(),
                object : AddDialogListener {
                    override fun onAddButtonClicked(item: ShoppingItem) {
                        viewModel.insertShoppingItem(item)
                    }
                },viewModel).show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            tvTotalPrice.text = viewModel.getTotalPrice().toString()
        }

        viewModel.isIncreaseClicked.observe(viewLifecycleOwner, Observer {
            if(it) {
                CoroutineScope(Dispatchers.Main).launch {
                    tvTotalPrice.text = viewModel.getTotalPrice().toString()
                }
            }
        })

        viewModel.isDeleteClicked.observe(viewLifecycleOwner, Observer {
            if(it) {
                CoroutineScope(Dispatchers.Main).launch {
                    tvTotalPrice.text = viewModel.getTotalPrice().toString()
                }
            }
        })

        viewModel.isAddClicked.observe(viewLifecycleOwner, Observer {
            if(it) {
                CoroutineScope(Dispatchers.Main).launch {
                    tvTotalPrice.text = viewModel.getTotalPrice().toString()
                }
            }
        })
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.notification->{

                if (networkInfo.isConnected()) {
                    findNavController().navigate(R.id.action_loacalListFragment_to_notificationFragment)
                } else {
                   Snackbar.make(requireView(),getString(R.string.u_cant_use_this_feature),Snackbar.LENGTH_SHORT).show()
                }

                true
            }
            else -> false
        }
    }

}