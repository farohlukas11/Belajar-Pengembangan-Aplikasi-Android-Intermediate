package com.dicoding.storyappsub1.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSignUp(
    var name: String,
    var email: String,
    var password: String
) : Parcelable
