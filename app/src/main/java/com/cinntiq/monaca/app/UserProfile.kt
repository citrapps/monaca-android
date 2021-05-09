package com.cinntiq.monaca.app

import android.net.Uri
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserProfile {
    var email: String?
        get() = Firebase.auth.currentUser?.email
        set(value) {
            Firebase.auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().apply {
                email = value
            }.build())
        }

    var displayName: String?
        get() = Firebase.auth.currentUser?.displayName
        set(value) {
            Firebase.auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().apply {
                displayName = value
            }.build())
        }

    var photoUrl: Uri?
        get() = Firebase.auth.currentUser?.photoUrl ?: Uri.EMPTY
        set(value) {
            Firebase.auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().apply {
                photoUrl = value
            }.build())
        }

    var isEmailVerified: Boolean = false
        get() = Firebase.auth.currentUser?.isEmailVerified ?: false
        private set

    var providerIds: List<String>? = null
        get() = Firebase.auth.currentUser?.providerData?.map { it.providerId }
            ?.filter { it != Monaca.Constant.AUTH_PROVIDER_FIREBASE_ID }
        private set
}
