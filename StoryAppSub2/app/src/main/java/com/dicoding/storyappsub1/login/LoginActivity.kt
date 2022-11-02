package com.dicoding.storyappsub1.login

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
import com.dicoding.storyappsub1.databinding.ActivityLoginBinding
import com.dicoding.storyappsub1.ViewModelFactory
import com.dicoding.storyappsub1.main.MainActivity
import com.dicoding.storyappsub1.model.Result
import com.dicoding.storyappsub1.model.UserLogin
import com.dicoding.storyappsub1.signup.MyEditPassword

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }
    private lateinit var myEditPassword: MyEditPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        myEditPassword = findViewById(R.id.passwordEditText)

        setupView()
        setupAction()
        playAnimation()

//        loginViewModel.isRegistred.observe(this) {
//            if (it == false) {
//                loginViewModel.loginResult.observe(this) { login ->
//                    addPreferencesUser(login.token)
//                }
//
//                val intentMain = Intent(this, MainActivity::class.java)
//                startActivity(intentMain)
//                finish()
//            }
//        }


//        loginViewModel.isLoading.observe(this) { bool ->
//            showLoading(bool)
//        }
//
//        loginViewModel.messageRegistred.observe(this) { mes ->
//            showToast(mes)
//        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(loginBinding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val title =
            ObjectAnimator.ofFloat(loginBinding.titleTextView, View.ALPHA, 1F).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(loginBinding.messageTextView, View.ALPHA, 1F).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(loginBinding.emailTextView, View.ALPHA, 1F).setDuration(500)
        val etEmail =
            ObjectAnimator.ofFloat(loginBinding.emailEditTextLayout, View.ALPHA, 1F)
                .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(loginBinding.passwordTextView, View.ALPHA, 1F).setDuration(500)
        val etPassword =
            ObjectAnimator.ofFloat(loginBinding.passwordEditTextLayout, View.ALPHA, 1F)
                .setDuration(500)
        val loginButton =
            ObjectAnimator.ofFloat(loginBinding.loginButton, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                email,
                etEmail,
                password,
                etPassword,
                loginButton
            )
            startDelay = 500
        }.start()
    }

    private fun setupAction() {
        loginBinding.loginButton.setOnClickListener {
            val email = loginBinding.emailEditText.text.toString()
            val password = myEditPassword.text.toString()

            when {
                email.isEmpty() -> loginBinding.emailEditTextLayout.error =
                    getString(R.string.error_email)
                password.isEmpty() && password.length < 6 -> loginBinding.passwordEditTextLayout.error =
                    getString(R.string.error_password)
                else -> loginViewModel.loginUser(UserLogin(email, password))
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> showLoading(true)
                                is Result.Success -> {
                                    showLoading(false)
                                    val loginData = result.data

                                    if (!loginData.error) {
                                        showLoading(false)
                                        addPreferencesUser(loginData.loginResult.token)
                                        showToast(loginData.message)

                                        finishAffinity()
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    showToast(getString(R.string.error_login))
                                }
                            }
                        } else {
                            showToast(getString(R.string.error_offline))
                        }
                    }
            }
        }
    }

    private fun addPreferencesUser(token: String) {
        loginViewModel.saveUser(token, false)
        loginViewModel.isLogin()
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
            loginBinding.progressBar.visibility = View.VISIBLE
        } else {
            loginBinding.progressBar.visibility = View.INVISIBLE
        }
    }
}