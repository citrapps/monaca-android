package com.cinntiq.monaca.ui.login

data class LoginFormState(
    val emailHelperResId: Int? = null,
    val passwordHelperResId: Int? = null,
    val isDataValid: Boolean = false,
    val isPasswordForgot: Boolean = false,
)
