package com.dicoding.storyappsub1.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.databinding.ActivitySignUpBinding
import com.dicoding.storyappsub1.login.LoginActivity
import com.dicoding.storyappsub1.model.UserSignUp

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpBinding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()
    private lateinit var myEditPassword: MyEditPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)

        myEditPassword = findViewById(R.id.passwordEditText)

        setupView()
        setupAction()
        playAnimation()

        signUpViewModel.isRegistred.observe(this) {
            if (it == false) {
                val intentLogin = Intent(this, LoginActivity::class.java)
                startActivity(intentLogin)

            } else {
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
            }
        }

        signUpViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        signUpViewModel.messageRegistred.observe(this) {
            if (it == null) {
                showToast(getString(R.string.error_signup))
            } else {
                showToast(it)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(signUpBinding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val title =
            ObjectAnimator.ofFloat(signUpBinding.titleTextView, View.ALPHA, 1F).setDuration(500)
        val name =
            ObjectAnimator.ofFloat(signUpBinding.nameTextView, View.ALPHA, 1F).setDuration(500)
        val etName =
            ObjectAnimator.ofFloat(signUpBinding.nameEditTextLayout, View.ALPHA, 1F)
                .setDuration(500)
        val email =
            ObjectAnimator.ofFloat(signUpBinding.emailTextView, View.ALPHA, 1F).setDuration(500)
        val etEmail =
            ObjectAnimator.ofFloat(signUpBinding.emailEditTextLayout, View.ALPHA, 1F)
                .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(signUpBinding.passwordTextView, View.ALPHA, 1F).setDuration(500)
        val etPassword =
            ObjectAnimator.ofFloat(signUpBinding.passwordEditTextLayout, View.ALPHA, 1F)
                .setDuration(500)
        val signUpButton =
            ObjectAnimator.ofFloat(signUpBinding.signupButton, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                etName,
                email,
                etEmail,
                password,
                etPassword,
                signUpButton
            )
            startDelay = 500
            start()
        }
    }

    private fun setupAction() {
        signUpBinding.signupButton.setOnClickListener {
            val name = signUpBinding.nameEditText.text.toString()
            val email = signUpBinding.emailEditText.text.toString()
            val password = myEditPassword.text.toString()

            when {
                name.isEmpty() -> signUpBinding.nameEditTextLayout.error =
                    getString(R.string.error_name)
                email.isEmpty() -> signUpBinding.emailEditTextLayout.error =
                    getString(R.string.error_email)
                else -> signUpViewModel.signUpUser(UserSignUp(name, email, password))
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            signUpBinding.progressBar.visibility = View.VISIBLE
        } else {
            signUpBinding.progressBar.visibility = View.INVISIBLE
        }
    }
}