package com.example.andproject.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.andproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.example.andproject.Constants
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.models.Account
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : BaseActivity(), View.OnClickListener  {

    private val auth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showFullScreen()
        initiateGoogleSignInClient()

        val btnGoogleSignIn = findViewById<SignInButton>(R.id.sign_in_button)
        val btnLogin = findViewById<Button>(R.id.btn_login_login_activity)
        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password_login_activity)
        val btnCreateAccount = findViewById<Button>(R.id.btn_create_account_login_activity)

        btnGoogleSignIn.setSize(SignInButton.SIZE_STANDARD)
        btnGoogleSignIn.setBackgroundResource(android.R.color.background_dark)

        btnGoogleSignIn.setOnClickListener(this);
        btnLogin.setOnClickListener(this)
        btnCreateAccount.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onClick(view: View?){
        if(view != null){
            when(view.id){
                R.id.btn_login_login_activity -> {
                    loginToAccount()
                }
                R.id.btn_create_account_login_activity -> {
                    startActivity(
                        Intent(this, CreateAccountActivity::class.java)
                    )
                }
                R.id.tv_forgot_password_login_activity -> {
                    startActivity(
                        Intent(this, ForgotPasswordActivity::class.java)
                    )
                }
                // Google sign in below
                R.id.sign_in_button -> {
                    googleSignIn()
                }
            }
        }
    }

    private fun initiateGoogleSignInClient(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.idToken))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    @Suppress("DEPRECATION")
    private fun googleSignIn() {
        showProgressDialog(resources.getString(R.string.sign_in_with_google))
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                loginWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                singingInFailure(e.message.toString())
            }
        } else{
            singingInFailure("Wrong request code = $requestCode")
        }
    }

    private fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    AccountRepository().getAccountDetails(this)

                } else {
                    singingInFailure(task.exception!!.message.toString())
                  }
            }
    }

    private fun loginToAccount(){
        if(validateAccountDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))

            val email = findViewById<EditText>(R.id.et_email_login_activity).text.toString().trim { it <= ' ' }
            val password = findViewById<EditText>(R.id.et_password_login_activity).text.toString().trim { it <= ' ' }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        AccountRepository().getAccountDetails(this)

                    } else {
                        singingInFailure(task.exception!!.message.toString())

                        Toast.makeText(this, task.exception!!.message.toString(),
                            Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener{ e->
                    singingInFailure(e.message.toString())
                }
        }
    }

    private fun validateAccountDetails(): Boolean {

        val email = findViewById<EditText>(R.id.et_email_login_activity)
        val password = findViewById<EditText>(R.id.et_password_login_activity)
        return when {
            TextUtils.isEmpty(email.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.login_success), false)
                true
            }
        }
    }

    fun accountLoggedInSuccess(account: Account){

        hideProgressDialog()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.EXTRA_ACCOUNT, account)
        startActivity(intent)
        finish()
    }

    fun singingInFailure(message: String){
        hideProgressDialog()
        Log.i(Constants.CORIDE_TAG, "Failed to Log in with Google, because of e = $message")
        showErrorSnackBar("Failed to Log in with Google! Error = $message", true)
    }

}

