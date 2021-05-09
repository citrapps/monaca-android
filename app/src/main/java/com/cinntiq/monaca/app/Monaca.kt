package com.cinntiq.monaca.app

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.facebook.appevents.AppEventsLogger

class Monaca : Application() {
    override fun onCreate() {
        super.onCreate()
        AppEventsLogger.activateApp(this)
    }

    object Constant {
        const val AUTH_PROVIDER_FACEBOOK_ID = "facebook.com"
        const val AUTH_PROVIDER_FIREBASE_ID = "firebase"
        const val AUTH_PROVIDER_GOOGLE_ID = "google.com"
        const val AUTH_PROVIDER_PASSWORD_ID = "password"
        const val REQUEST_CODE_SIGN_IN_FACEBOOK = 0
        const val REQUEST_CODE_SIGN_IN_GOOGLE = 1
        const val REQUEST_ID_TOKEN_GOOGLE = "298273460888-4i64thbkvldk3i0lsm1jptlepap2jnrd.apps.googleusercontent.com"
    }

    companion object {
        fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    afterTextChanged.invoke(editable.toString())
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }
    }
}
