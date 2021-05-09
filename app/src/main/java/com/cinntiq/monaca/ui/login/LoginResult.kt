package com.cinntiq.monaca.ui.login

import com.google.firebase.auth.UserInfo

data class LoginResult(
    val successMessageResId: Int? = null,
    val errorMessageResId: Int? = null,
    val emailNotVerifiedResId: Int? = null,
    val sendPasswordResetEmailMessageResId: Int? = null,
    val messageParam: Any? = null,
    val providerIds: List<String>? = null,
)
