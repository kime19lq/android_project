package com.example.andproject.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.andproject.Constants
import com.example.andproject.R
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.models.Account
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_account.*


class CreateAccountActivity : BaseActivity(), View.OnClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val btnLogin = findViewById<Button>(R.id.btn_login_create_account_activity)
        val btnCreateAccount = findViewById<Button>(R.id.btn_create_account_create_account_activity)

        btnCreateAccount.setOnClickListener(this)
        btnLogin.setOnClickListener(this)

    }

    override fun onClick(view: View?){

        if(view != null){
            when(view.id){
                R.id.btn_create_account_create_account_activity -> {
                    createAccount()
                }
                R.id.btn_login_create_account_activity -> {
                    onBackPressed()
                }
            }
        }
    }

    private fun validateAccountDetails(): Boolean {
        val firstName = findViewById<EditText>(R.id.et_firstname_create_account_activity)
        val lastName = findViewById<EditText>(R.id.et_lastname_create_account_activity)
        val email = findViewById<EditText>(R.id.et_email_create_account_activity)
        val password = findViewById<EditText>(R.id.et_password_create_account_activity)
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password_create_account_activity)
        val termsConditions = findViewById<CheckBox>(R.id.cb_terms_and_condition_create_account_activity)
        return when {
            TextUtils.isEmpty(firstName.text.toString().trim { it <= ' '})
                    || firstName.length() <= Constants.MINIMUM_LENGTH-> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_firstname), true)
                false
            }
            TextUtils.isEmpty(lastName.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_lastname), true)
                false
            }
            lastName.length() <= Constants.MINIMUM_LENGTH -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_complete_lastname), true)
                false
            }
            TextUtils.isEmpty(email.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(confirmPassword.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }
            password.text.toString().trim { it <= ' '} != confirmPassword.text.toString().trim { it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_confirm_mismatch), true)
                false
            }
            !termsConditions.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_conditions), true)
                false
            }
            else -> {
                //showErrorSnackBar(resources.getString(R.string.account_creation_correct), false)
                true
            }
        }
    }

    private fun createAccount(){
        if(validateAccountDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))

            val email = findViewById<EditText>(R.id.et_email_create_account_activity).text.toString().trim { it <= ' ' }
            val password = findViewById<EditText>(R.id.et_password_create_account_activity).text.toString().trim { it <= ' ' }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val fbUser: FirebaseUser = task.result!!.user!!
                        val account = Account(
                            fbUser.uid,
                            et_firstname_create_account_activity.text.toString().trim { it <= ' ' },
                            et_lastname_create_account_activity.text.toString().trim { it <= ' ' },
                            et_username_create_account_activity.text.toString().trim { it <= ' '},
                            email,
                            et_telephone_create_account_activity.text.toString().trim { it <= ' '}.toLong(),
                            1
                        )
                        AccountRepository().createAccount(this, account)
                    } else {
                        hideProgressDialog()
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Creating account with email and password failed.",
                            Toast.LENGTH_LONG).show()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
                .addOnFailureListener{ e->
                    hideProgressDialog()
                    showErrorSnackBar("Create account in failed, because of:: $e", true)
                }
        }
    }

    fun createAccountSuccess(){
        hideProgressDialog()
        auth.signOut()
        finish()
        showErrorSnackBar("New Account created successfully with email: ${et_email_create_account_activity.text}", false)
    }

}