package com.shorman.shoppinglist.ui

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.db.ShoppingItem
import kotlinx.android.synthetic.main.dialog_add_shopping_item.*
import java.lang.Exception

class AddShoppingItemDialog(context: Context, var addDialogListener: AddDialogListener,val viewModel: MainViewModel) :
    AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_shopping_item)

        btnSave.setOnClickListener {
            try {
                val name = etName.text.toString()
                val amount = etAmount.text.toString().toInt()
                val price = etPrice.text.toString().toFloat()

                if(name.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.please_enter_vaild_name), Toast.LENGTH_SHORT).show()
                }
                else {
                    val item = ShoppingItem(amount, name, price)
                    addDialogListener.onAddButtonClicked(item)
                    dismiss()
                }

            }catch (e:Exception){
                Toast.makeText(context, context.getString(R.string.please_enter_vaild_amount), Toast.LENGTH_SHORT).show()
            }

        }

        btnCancel.setOnClickListener {
            cancel()
        }
    }
}