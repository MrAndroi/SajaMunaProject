package com.shorman.shoppinglist.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.adapters.ShoppingAdapter
import com.shorman.shoppinglist.db.ShoppingItem
import com.shorman.shoppinglist.ui.AddDialogListener
import com.shorman.shoppinglist.ui.AddShoppingItemDialog
import com.shorman.shoppinglist.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_loacal_list.*


@AndroidEntryPoint
class LoacalListFragment :Fragment(R.layout.fragment_loacal_list) {

    val viewModel:MainViewModel by viewModels()
    lateinit var shoppingAdapter: ShoppingAdapter
    
    override fun onResume() {
        super.onResume()
        tvUserName.text = FirebaseAuth.getInstance().currentUser?.displayName
    }

    override fun onPause() {
        super.onPause()
        tvUserName.text = ""
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        shoppingAdapter = ShoppingAdapter(viewModel, {

            Snackbar.make(requireView(), getString(R.string.long_click), Snackbar.LENGTH_SHORT)
                .show()

        }, { shoppingItem, item ->
            openEditDialog(item)
            mainContainer.setOnClickListener {
                exitEditDialog(item)
            }
            tvTitleEdit.text = getString(R.string.edit) + shoppingItem.name
            etNameEdit.setText(shoppingItem.name)
            etPriceEdit.setText(shoppingItem.price.toString())
            etAmountEdit.setText(shoppingItem.quantity.toString())

            val oldName = etNameEdit.text.toString()
            val oldPrice = etPriceEdit.text.toString().toFloat()
            val oldAmount = etAmountEdit.text.toString().toInt()

            btnSaveEdit.setOnClickListener {
                val newName = etNameEdit.text.toString()
                val newPrice = etPriceEdit.text.toString()
                val newAmount = etAmountEdit.text.toString()

                if (newName == "" || newAmount == "" || newPrice == "") {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.u_cant_leave_empty_filed),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    shoppingItem.name = newName
                    shoppingItem.price = newPrice.toFloat()
                    shoppingItem.quantity = newAmount.toInt()
                    shoppingItem.totalPrice = shoppingItem.price * shoppingItem.quantity

                    viewModel.updateItem(shoppingItem)

                    exitEditDialog(item)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.saved_successfully_as) + newName,
                        Snackbar.LENGTH_LONG
                    ).setAction("Undo") {
                        shoppingItem.name = oldName
                        shoppingItem.price = oldPrice
                        shoppingItem.quantity = oldAmount
                        viewModel.updateItem(shoppingItem)
                    }.show()
                }

            }

            btnCancelEdit.setOnClickListener {
                exitEditDialog(item)
            }
            return@ShoppingAdapter true
        })

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
            }

        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvShoppingItems)

        viewModel.getShoppingItems().observe(viewLifecycleOwner, Observer {
            shoppingAdapter.differ.submitList(it)
            rvShoppingItems.adapter = shoppingAdapter

        })

        fabSend.setOnClickListener {
            if (isNetworkAvailable()) {
                enterTransition = MaterialElevationScale(true).apply {
                    duration=500
                }
                exitTransition = MaterialElevationScale(true).apply {
                    duration=500
                }
                findNavController().navigate(R.id.action_loacalListFragment_to_searchListActivity)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.u_cant_use_this_feature),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        fab.setOnClickListener {
            AddShoppingItemDialog(
                requireActivity(),
                object : AddDialogListener {
                    override fun onAddButtonClicked(item: ShoppingItem) {
                        viewModel.insertShoppingItem(item)
                    }
                }, viewModel
            ).show()
        }

        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                tvTotalPrice.text = 0f.toString()
            } else {
                tvTotalPrice.text = it.toString()
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.notification -> {

                if (isNetworkAvailable()) {
                    findNavController().navigate(R.id.action_loacalListFragment_to_notificationFragment)
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.u_cant_use_this_feature),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                true
            }
            else -> false
        }
    }

    private fun openEditDialog(item: View){
        val transform = MaterialContainerTransform().apply {
            startView = item
            endView  = alertDialogEditContainer
            duration = 500
            addTarget(alertDialogEditContainer)
        }
        alertDialogEditContainer.visibility = View.VISIBLE
        shadowImage.visibility = View.VISIBLE
        rvShoppingItems.visibility= View.INVISIBLE
        fab.visibility = View.INVISIBLE
        fabSend.visibility = View.INVISIBLE
        TransitionManager.beginDelayedTransition(mainContainer, transform)

    }

    private fun exitEditDialog(item: View){
        val transform = MaterialContainerTransform().apply {
            startView = alertDialogEditContainer
            endView  = item
            duration = 500
            addTarget(item)
        }
        alertDialogEditContainer.visibility = View.INVISIBLE
        shadowImage.visibility = View.INVISIBLE
        rvShoppingItems.visibility= View.VISIBLE
        fab.visibility = View.VISIBLE
        fabSend.visibility = View.VISIBLE

        TransitionManager.beginDelayedTransition(mainContainer, transform)

    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
        return isConnected
    }

}