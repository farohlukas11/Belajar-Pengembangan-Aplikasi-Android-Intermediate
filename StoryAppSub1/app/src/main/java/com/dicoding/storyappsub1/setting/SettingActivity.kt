package com.dicoding.storyappsub1.setting

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.storyappsub1.databinding.ActivitySettingBinding
import com.dicoding.storyappsub1.welcome.WelcomeActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var settingBinding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingBinding.root)

        setupView()

        settingViewModel.getThemeSetting().observe(this) { isDarkActive ->
            setTheme(isDarkActive)
        }

        settingBinding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        settingBinding.btnLogout.setOnClickListener {
            logOut()

            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

    }

    private fun logOut() {
        settingViewModel.saveUser("", false)
        settingViewModel.isLogout()
        settingViewModel.saveThemeSetting(false)
    }

    private fun setTheme(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            settingBinding.switchTheme.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            settingBinding.switchTheme.isChecked = false
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
}