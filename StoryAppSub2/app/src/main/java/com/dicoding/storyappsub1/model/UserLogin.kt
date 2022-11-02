package com.dicoding.storyappsub1.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLogin(
    var email: String,
    var password: String
) : Parcelable