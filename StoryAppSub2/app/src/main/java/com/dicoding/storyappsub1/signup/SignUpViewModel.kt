package com.dicoding.storyappsub1.signup

import androidx.lifecycle.ViewModel
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.model.UserSignUp

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun signUpUser(userSignUp: UserSignUp) = userRepository.signUpUser(userSignUp)

}