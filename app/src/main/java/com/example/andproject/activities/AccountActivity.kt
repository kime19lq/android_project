package com.example.andproject.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.andproject.R
import com.example.andproject.firestore.AccountRepository
import com.example.andproject.models.Account
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity :  BaseActivity(), View.OnClickListener {

    private lateinit var currentAccount: Account
    private val auth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        toLoginActivity()
        getAccountDetails()

        val btnLogout = findViewById<Button>(R.id.btn_logout_account_activity)
        btnLogout.setOnClickListener(this)

        showBottomNavigation(R.id.account)

    }

    override fun onClick(view: View?){
        if(view != null){
            when(view.id){
                R.id.btn_logout_account_activity -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    signOutFirebase()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toLoginActivity()
        getAccountDetails()
    }

    private fun initiateGoogleSignInClient(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.idToken))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signOutFirebase (){

        initiateGoogleSignInClient()

        auth.signOut()
        signOutGoogle(GoogleSignIn.getLastSignedInAccount(this) != null)

        hideProgressDialog()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun signOutGoogle(signedInWithGoogle: Boolean){
        if(signedInWithGoogle) {
            googleSignInClient.signOut()
            revokeAccess()
        }
    }

    private fun revokeAccess() {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                showErrorSnackBar("Deleted data Google stored!", false)
            }
    }

    private fun getAccountDetails() {
        toLoginActivity()
        AccountRepository().getAccountDetails(this)
    }

    fun accountDetailsSuccess(account: Account){
        showProgressDialog("Fetching Account Details...")
        if(account != null) {
            hideProgressDialog()
            currentAccount = account
            tv_username_account_activity.text = "Username: ${account.username}"
            tv_fullname_account_activity.text = "Fullname: ${account.firstname} ${account.lastname}"
            tv_email_account_activity.text = "Email: ${account.email}"
            tv_telephone_account_activity.text = "Phone Nr: ${account.mobile}"
        } else {
            showErrorSnackBar("Failed to Load account!", true)
        }
    }
}