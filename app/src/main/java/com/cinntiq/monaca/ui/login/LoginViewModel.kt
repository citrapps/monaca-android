package com.cinntiq.monaca.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.cinntiq.monaca.data.LoginRepository
import com.cinntiq.monaca.app.Result
import com.cinntiq.monaca.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun signIn(email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            loginRepository.signIn(email, password).run {
                if (this is Result.Success) {
                    if (data.isEmailVerified) {
                        _loginResult.value =
                            LoginResult(successMessageResId = R.string.toast_welcome, messageParam = data.email)
                    } else {
                        _loginResult.value = LoginResult(
                            emailNotVerifiedResId = R.string.toast_email_not_verified,
                            messageParam = data.email,
                            providerIds = data.providerIds
                        )
                    }
                } else if (this is Result.Error) {
                    _loginResult.value =
                        LoginResult(errorMessageResId = R.string.toast_sign_in_failed, messageParam = exception.message)
                    _loginForm.value =
                        LoginFormState(
                            emailHelperResId = R.string.helper_text_email_invalid,
                            passwordHelperResId = R.string.helper_text_password_invalid,
                            isDataValid = isEmailValid(email) && isPasswordValid(password),
                            isPasswordForgot = isEmailValid(email)
                        )
                }
            }
        }
    }

    fun signIn(idToken: String, requestCode: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            loginRepository.signIn(idToken, requestCode).run {
                if (this is Result.Success) {
                    if (data.isEmailVerified) {
                        _loginResult.value =
                            LoginResult(successMessageResId = R.string.toast_welcome, messageParam = data.email)
                    } else {
                        _loginResult.value = LoginResult(
                            emailNotVerifiedResId = R.string.toast_email_not_verified,
                            messageParam = data.email,
                            providerIds = data.providerIds
                        )
                    }
                } else if (this is Result.Error) {
                    _loginResult.value =
                        LoginResult(errorMessageResId = R.string.toast_sign_in_failed, messageParam = exception.message)
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        CoroutineScope(Dispatchers.Main).launch {
            loginRepository.sendPasswordResetEmail(email).run {
                if (this is Result.Success) {
                    _loginResult.value = LoginResult(
                        sendPasswordResetEmailMessageResId = R.string.toast_password_reset_email_sent,
                        messageParam = data
                    )
                } else if (this is Result.Error) {
                    _loginResult.value = LoginResult(
                        sendPasswordResetEmailMessageResId = R.string.toast_password_reset_email_sent_failed,
                        messageParam = exception.message
                    )
                }
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        _loginForm.value = LoginFormState(
            emailHelperResId = if (email.isEmpty()) null else _loginForm.value?.emailHelperResId,
            passwordHelperResId = if (password.isEmpty()) null else _loginForm.value?.passwordHelperResId,
            isDataValid = isEmailValid(email) && isPasswordValid(password),
            isPasswordForgot = isEmailValid(email)
        )
    }

    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String) = password.length >= 8
}
