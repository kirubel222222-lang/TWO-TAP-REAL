package com.example.network

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthHelper {
    
    fun getAuth(context: Context): FirebaseAuth? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                // Return null if not initialized to prevent crash
                null
            } else {
                FirebaseAuth.getInstance()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    @Suppress("DEPRECATION")
    fun getGoogleSignInClient(context: Context) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("296531380597-nmk3113v67cfc8g01hr00rrifgco38el.apps.googleusercontent.com")
            .requestEmail()
            .build()
    )
}
