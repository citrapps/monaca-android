package com.cinntiq.monaca.data

import com.cinntiq.monaca.app.Monaca
import com.cinntiq.monaca.app.Result
import com.cinntiq.monaca.app.UserProfile
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginDataSource {
    private val user = UserProfile()

    suspend fun signIn(email: String, password: String) = try {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { sendEmailVerification() }
            .await()
        Result.Success(user)
    } catch (e: Exception) {
        when (e) {
            is FirebaseAuthInvalidUserException -> {
                try {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { sendEmailVerification() }
                        .await()
                    Result.Success(user)
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
            else -> Result.Error(e)
        }
    }

    suspend fun signIn(idToken: String, requestCode: Int) = try {
        when (requestCode) {
            Monaca.Constant.REQUEST_CODE_SIGN_IN_GOOGLE -> {
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            }
            Monaca.Constant.REQUEST_CODE_SIGN_IN_FACEBOOK -> {
                Firebase.auth.signInWithCredential(FacebookAuthProvider.getCredential(idToken))
                    .addOnSuccessListener { sendEmailVerification() }
                    .await()
            }
        }
        Result.Success(user)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun sendPasswordResetEmail(email: String) = try {
        Firebase.auth.sendPasswordResetEmail(email).await()
        Result.Success(email)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private fun sendEmailVerification() {
        Firebase.auth.currentUser?.run {
            if (!user.isEmailVerified) {
                sendEmailVerification()
            }
        }
    }
}
