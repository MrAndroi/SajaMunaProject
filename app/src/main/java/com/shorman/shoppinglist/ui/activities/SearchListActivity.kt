package com.shorman.shoppinglist.ui.activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.Slide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.adapters.UserListAdapter
import com.shorman.shoppinglist.db.ShoppingItem
import com.shorman.shoppinglist.models.NotificationData
import com.shorman.shoppinglist.models.PushNotification
import com.shorman.shoppinglist.models.Sender
import com.shorman.shoppinglist.models.User
import com.shorman.shoppinglist.retrofit.RetrofitInstance
import com.shorman.shoppinglist.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_search_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

const val TOPIC = "/topics/myTopics"

@AndroidEntryPoint
class SearchListActivity:Fragment(R.layout.activity_search_list){

    private val viewModel:MainViewModel by viewModels()
    private val adapter = UserListAdapter()
    private var userName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid
    private  var mUsers: List<User>? = null
    private var shoppingList : List<ShoppingItem> = ArrayList()
    private lateinit var sender:Sender
    private  lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.fabSend)
            endView = searchContainer
            duration = 500
            scrimColor = Color.TRANSPARENT
            containerColor = Color.BLACK
        }
        returnTransition = Slide().apply {
            addTarget(searchContainer)
            duration=500
        }

        MobileAds.initialize(requireContext())
        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd.adUnitId = "ca-app-pub-1259439794687303/3570107631"
        mInterstitialAd.loadAd(AdRequest.Builder().build())


        viewModel.getShoppingItems().observe(viewLifecycleOwner, Observer {
            shoppingList= it
             sender = Sender(userName,userId!!,System.currentTimeMillis(),shoppingList)
        })

        mUsers = ArrayList()
        progressBar.visibility = View.GONE


        etSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                searchForUser(s.toString().toLowerCase(Locale.getDefault()))
                if(etSearch.text.equals("")){
                    (mUsers as ArrayList<User>).clear()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                progressBar.visibility = View.VISIBLE
            }
        })


        adapter.onItemClick {

            val builder = AlertDialog.Builder(requireContext(),R.style.AlertDialogCustom)
            builder.setTitle(getString(R.string.confirm))
            builder.setMessage(getString(R.string.are_u_sure_u_want)+it.name)

            builder.setPositiveButton(android.R.string.yes) { _ , _ ->

                val sendListRef = FirebaseDatabase.getInstance()
                    .reference.child("/users/${it.uid}/shoppingList")

                val senderList = it.shoppingList
                senderList?.add(sender)

                if(shoppingList.isEmpty()){
                    Snackbar.make(requireView(),getString(R.string.u_dont_have_any_list_to_send),Snackbar.LENGTH_SHORT).show()
                }else {
                    sendListRef.setValue(senderList)
                    val title = getString(R.string.you_have_new_list)
                    val massage  = getString(R.string.new_list_recived)
                    PushNotification(
                        NotificationData(title, massage),
                        it.token!!
                    ).also {pushedNotification ->
                        sendNotification(pushedNotification)
                    }

                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    }

                    Toast.makeText(requireContext(),getString(R.string.list_sent_to)+it.name,Toast.LENGTH_LONG).show()
                }

            }

            builder.setNegativeButton(android.R.string.no) { dialog, _ ->
               dialog.dismiss()
            }
            builder.show()
        }

    }

    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {

        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.e("msg",response.message())
            }else{
                Log.e("msg",response.errorBody().toString())
            }
        }
        catch (e: Exception){
            Log.e("Error",e.message.toString())
        }

    }


    private fun searchForUser(name:String){

        if(name == ""){
            (mUsers as ArrayList<User>).clear()
            adapter.notifyDataSetChanged()
            if (tvSearchForUser != null) {
                tvSearchForUser.visibility = View.VISIBLE
            }
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
        }
        else {
            val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid

            val queryRef = FirebaseDatabase.getInstance().reference.child("users")
                .orderByChild("name")
                .startAt(name)
                .endAt(name + "\uf8ff")

            queryRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    (mUsers as ArrayList<User>).clear()

                    for (data in snapshot.children) {

                        val user = data.getValue(User::class.java)
                        if (!(user?.uid.equals(firebaseUserId))) {
                            (mUsers as ArrayList<User>).add(user!!)
                        }
                    }
                    adapter.differ.submitList(mUsers)
                    if (rvSearch != null) {
                        rvSearch.adapter = adapter
                    }

                    if (progressBar != null) {
                        progressBar.visibility = View.GONE
                    }
                    if (tvSearchForUser != null) {
                        tvSearchForUser.visibility = View.GONE
                    }
                }

            })

        }

    }
}