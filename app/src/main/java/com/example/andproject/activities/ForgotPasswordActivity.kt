package com.example.andproject.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.andproject.Constants
import com.example.andproject.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity :  BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        showFullScreen()
        setupActionBar(R.id.toolbar_forgot_password_activity)

        val btnResetPassword = findViewById<Button>(R.id.btn_reset_password_forgot_password_activity)

        btnResetPassword.setOnClickListener(this)
    }

    override fun onClick(view: View?){

        if(view != null){
            when(view.id){
                R.id.btn_reset_password_forgot_password_activity -> {
                    resetPasswordUsingFirebase()
                }
            }
        }
    }

    private fun resetPasswordUsingFirebase(){
        if(validateResetPasswordDetails()){
            showProgressDialog("Sending reset e-mail...")
            val email = findViewById<EditText>(R.id.et_email_forgot_password_activity).text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        resetPasswordSuccess()
                    } else{
                        Toast.makeText(this, task.exception!!.message.toString(),
                            Toast.LENGTH_LONG).show()
                        resetPasswordFailure(task.exception!!.message.toString())
                        Log.i(Constants.CORIDE_TAG, task.exception!!.message.toString())
                    }
                }
                .addOnFailureListener{ e->
                    resetPasswordFailure(e.message.toString())

                }
        }
    }

    private fun validateResetPasswordDetails(): Boolean {

        val email = findViewById<EditText>(R.id.et_email_forgot_password_activity)
        val password = findViewById<EditText>(R.id.et_new_password_forgot_password_activity)
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password_forgot_password_activity)
        return when {
            TextUtils.isEmpty(email.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(confirmPassword.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.login_success), false)
                true
            }
        }
    }

    private fun resetPasswordSuccess(){
        hideProgressDialog()
        Toast.makeText(baseContext, "Reset password sent email, successful",
            Toast.LENGTH_LONG).show()
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }

    private fun resetPasswordFailure(message: String){
        hideProgressDialog()
        showErrorSnackBar(message, true)
    }

}