package com.shorman.shoppinglist.ui.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.models.User
import com.shorman.shoppinglist.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_change_nickname.*
import kotlinx.android.synthetic.main.dialog_change_nickname.view.*
import kotlinx.android.synthetic.main.fragment_loacal_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val REQUEST_CODE_LOGIN = 10

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    lateinit var providers:List<AuthUI.IdpConfig>
    var userId = ""
    var userId2 =""
    val firebaseAuthListener = FirebaseAuth.getInstance().addAuthStateListener {
        userId2 = it.currentUser?.uid.toString()
    }

    override fun onResume() {
        super.onResume()
        tvUserName.text = FirebaseAuth.getInstance().currentUser?.displayName
    }

    override fun onPause() {
        super.onPause()
        tvUserName.text = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        if(FirebaseAuth.getInstance().currentUser == null) {
            showSignIn()
        }



    }

    private fun showSignIn(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setTheme(R.style.AppTheme)
            .setAvailableProviders(providers)
            .build(), REQUEST_CODE_LOGIN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK){

            val user = FirebaseAuth.getInstance().currentUser
            userId = user?.uid!!
            var token = ""
            val ref = FirebaseDatabase.getInstance().getReference("/users/$userId")
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                token = it.token
            }
            val userRef = FirebaseDatabase.getInstance().reference.child("users")
            userRef.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(userId)){
                        viewModel.deleteTable()
                        ref.child("token").setValue(token)
                    }else{
                        val userToDataBase = User(user.displayName?.toLowerCase(),userId,user.displayName?.toLowerCase(),token)
                        ref.setValue(userToDataBase)
                        viewModel.deleteTable()
                    }
                }

            })



        }else{
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.signOut ->{
                FirebaseAuth.getInstance().signOut()
                showSignIn()
                if(this.findNavController(R.id.newsNavHost).currentDestination?.id == R.id.notificationFragment){
                    this.findNavController(R.id.newsNavHost).navigate(R.id.action_notificationFragment_to_loacalListFragment)
                }
                else if(this.findNavController(R.id.newsNavHost).currentDestination?.id == R.id.searchListActivity){
                    this.findNavController(R.id.newsNavHost).navigate(R.id.action_searchListActivity_to_loacalListFragment)
                }
                 true
            }

            R.id.changeNickname ->{

                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_nickname, null)

                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)

                val  mAlertDialog = mBuilder.show()


                val nicknameRef = FirebaseDatabase.getInstance().reference.child("users")
                    .child(userId2).child("search")
                nicknameRef.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val lastNickname = snapshot.getValue(String::class.java)
                        mDialogView.etChangeNickName.setHint(getString(R.string.your_nickname_now) +lastNickname)
                        mDialogView.progressBar3.visibility = View.GONE
                    }

                })

                mDialogView.btnSave.setOnClickListener {
                    val nickname = mDialogView.etChangeNickName.text.toString()

                    if(nickname.length > 5) {
                        mDialogView.progressBar3.visibility =View.VISIBLE
                        CoroutineScope(Dispatchers.IO).launch {
                            nicknameRef.setValue(nickname)
                        }
                        Snackbar.make(mDialogView, getString(R.string.new_nickname_saved), Snackbar.LENGTH_LONG).show()
                        mDialogView.progressBar3.visibility= View.GONE
                    }else{
                        Snackbar.make(mDialogView, getString(R.string.must), Snackbar.LENGTH_LONG).show()
                    }

                }
                mDialogView.btnCancel.setOnClickListener {
                    mAlertDialog.dismiss()
                }

                true
            }

            else ->  false
        }
    }




}