package com.example.capstone.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.utils.Constant.Companion.LOGIN_CREDENTIAL
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.google_sign_in_button).setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            //startActivityForResult(signInIntent, Constant.RC_SIGN_IN)
            startForResult.launch(signInIntent)
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                }
            }
        }
    //Deprecated
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
       if (requestCode == Constant.RC_SIGN_IN) {
           val task = GoogleSignIn.getSignedInAccountFromIntent(data)
           try {
               // Google Sign In was successful, authenticate with Firebase
               val account = task.getResult(ApiException::class.java)
               firebaseAuthWithGoogle(account.idToken!!)
           } catch (e: ApiException) {
               // Google Sign In failed, update UI appropriately
           }
       }
   }*/

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, /*accessToken=*/ null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                Toast.makeText(this, ""+task, Toast.LENGTH_SHORT).show()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    val email = user?.email.toString()
                    val name = user?.displayName.toString()

                    //Storing Email and Name in Shared preference
                    val sharedPreference =
                        getSharedPreferences(LOGIN_CREDENTIAL, Context.MODE_PRIVATE)
                    val editor = sharedPreference.edit()
                    editor.putString("email", email)
                    editor.putString("name", name)
                    editor.apply()

                    //Send data
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("Email", email)
                    intent.putExtra("Name", name)
                    startActivity(intent)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity, "Authentication Failed.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("Data", "Failed")
                    //updateUI(null)
                }
            }
    }

}