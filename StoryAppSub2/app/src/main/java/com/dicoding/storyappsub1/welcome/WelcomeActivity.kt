package com.dicoding.storyappsub1.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.databinding.ActivityWelcomeBinding
import com.dicoding.storyappsub1.login.LoginActivity
import com.dicoding.storyappsub1.signup.SignUpActivity

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bindingWelcome: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingWelcome = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(bindingWelcome.root)

        setupView()
        bindingWelcome.apply {
            signupButton.setOnClickListener(this@WelcomeActivity)
            loginButton.setOnClickListener(this@WelcomeActivity)
        }
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(bindingWelcome.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(bindingWelcome.loginButton, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(bindingWelcome.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(bindingWelcome.titleTextView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(bindingWelcome.descTextView, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
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

    override fun onClick(v: View?) {
        val intent: Intent

        when (v?.id) {
            R.id.signupButton -> {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)

            }
            R.id.loginButton -> {
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}