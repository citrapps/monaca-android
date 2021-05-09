package com.cinntiq.monaca.data

import com.cinntiq.monaca.app.Result
import com.cinntiq.monaca.app.UserProfile

class LoginRepository(val dataSource: LoginDataSource) {
    var user: UserProfile? = null
        private set

    suspend fun signIn(username: String, password: String) = dataSource.signIn(username, password).also {
        if (it is Result.Success) {
            user = it.data
        }
    }

    suspend fun signIn(idToken: String, requestCode: Int) = dataSource.signIn(idToken, requestCode).also {
        if (it is Result.Success) {
            user = it.data
        }
    }

    suspend fun sendPasswordResetEmail(email: String) = dataSource.sendPasswordResetEmail(email)
}
