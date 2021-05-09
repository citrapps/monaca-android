package com.cinntiq.monaca.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cinntiq.monaca.R
import com.cinntiq.monaca.app.Monaca
import com.cinntiq.monaca.app.Monaca.Companion.afterTextChanged
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tilEmail = findViewById<TextInputLayout>(R.id.text_input_layout_email)
        val tietEmail = findViewById<TextInputEditText>(R.id.text_input_edit_text_email)
        val tilPassword = findViewById<TextInputLayout>(R.id.text_input_layout_password)
        val tietPassword = findViewById<TextInputEditText>(R.id.text_input_edit_text_password)
        val btnSignIn = findViewById<MaterialButton>(R.id.button_sign_in)
        val btnForgotPassword = findViewById<MaterialButton>(R.id.button_forgot_password)
        val btnSignInWithGoogle = findViewById<MaterialButton>(R.id.button_sign_in_with_google)
        val btnSignInWithFacebook = findViewById<MaterialButton>(R.id.button_sign_in_with_facebook)
        val pbLoading = findViewById<ProgressBar>(R.id.progress_bar_loading)

        callbackManager = CallbackManager.Factory.create()
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java).apply {
            loginFormState.observe(this@LoginActivity, Observer {
                it?.run {
                    tilEmail.helperText = emailHelperResId?.run { getString(this) }
                    tilPassword.helperText = passwordHelperResId?.run { getString(this) }
                    btnSignIn.isEnabled = isDataValid
                    btnForgotPassword.isEnabled = isPasswordForgot
                } ?: return@Observer
            })

            loginResult.observe(this@LoginActivity, Observer {
                it?.run {
                    pbLoading.visibility = View.GONE

                    if (successMessageResId != null) {
                        Toast.makeText(
                            applicationContext,
                            String.format(getString(successMessageResId, messageParam)),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    if (errorMessageResId != null) {
                        Toast.makeText(
                            applicationContext,
                            String.format(getString(errorMessageResId, messageParam)),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    if (emailNotVerifiedResId != null) {
                        Toast.makeText(
                            applicationContext,
                            String.format(getString(emailNotVerifiedResId, messageParam, providerIds.toString())),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    if (sendPasswordResetEmailMessageResId != null) {
                        Toast.makeText(
                            applicationContext,
                            String.format(getString(sendPasswordResetEmailMessageResId, messageParam)),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    setResult(Activity.RESULT_OK)
                } ?: return@Observer
            })
        }

        tietEmail.afterTextChanged {
            loginViewModel.loginDataChanged(tietEmail.text.toString(), tietPassword.text.toString())
        }

        tietPassword.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(tietEmail.text.toString(), tietPassword.text.toString())
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> loginViewModel.signIn(tietEmail.text.toString(), tietPassword.text.toString())
                }
                false
            }
        }

        btnSignIn.setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            loginViewModel.signIn(tietEmail.text.toString(), tietPassword.text.toString())
        }

        btnForgotPassword.setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            loginViewModel.sendPasswordResetEmail(tietEmail.text.toString())
        }

        btnSignInWithGoogle.setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            startActivityForResult(
                GoogleSignIn.getClient(
                    this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Monaca.Constant.REQUEST_ID_TOKEN_GOOGLE)
                        .requestEmail()
                        .build()
                ).signInIntent, Monaca.Constant.REQUEST_CODE_SIGN_IN_GOOGLE
            )
        }

        btnSignInWithFacebook.setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<com.facebook.login.LoginResult> {
                    override fun onSuccess(loginResult: com.facebook.login.LoginResult) {
                        loginViewModel.signIn(loginResult.accessToken.token, Monaca.Constant.REQUEST_CODE_SIGN_IN_FACEBOOK)
                    }

                    override fun onCancel() {
                        pbLoading.visibility = View.GONE
                    }

                    override fun onError(error: FacebookException) {
                        pbLoading.visibility = View.GONE
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Monaca.Constant.REQUEST_CODE_SIGN_IN_GOOGLE -> {
                GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)?.idToken?.let { idToken ->
                    loginViewModel.signIn(idToken, requestCode)
                }
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
