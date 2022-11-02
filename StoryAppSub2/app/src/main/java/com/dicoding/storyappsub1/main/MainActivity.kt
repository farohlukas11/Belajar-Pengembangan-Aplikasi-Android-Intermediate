package com.dicoding.storyappsub1.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.ViewModelFactory
import com.dicoding.storyappsub1.add_story.AddStoryActivity
import com.dicoding.storyappsub1.detail.DetailActivity
import com.dicoding.storyappsub1.adapter.LoadingStateAdapter
import com.dicoding.storyappsub1.adapter.StoriesListAdapter
import com.dicoding.storyappsub1.databinding.ActivityMainBinding
import com.dicoding.storyappsub1.maps.MapsActivity
import com.dicoding.storyappsub1.model.ListStoryItem
import com.dicoding.storyappsub1.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setupView()

        mainViewModel.mediator.observe(this) {}
        mainViewModel.user.observe(this) { tokenUser ->
            if (tokenUser != null) {
                getData(tokenUser)
            }
        }

        mainBinding.fabMap.setOnClickListener {
            val intentMap = Intent(this, MapsActivity::class.java)
            startActivity(intentMap)
        }

        mainBinding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        mainBinding.fabSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun getData(token: String) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainBinding.rvStories.layoutManager = GridLayoutManager(this, 2)
        } else {
            mainBinding.rvStories.layoutManager = LinearLayoutManager(this)
        }

        val adapter = StoriesListAdapter()
        mainBinding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.getStories(token).observe(this) {
            showLoading(true)
            if (it != null) {
                showLoading(false)
                adapter.submitData(lifecycle, it)

                adapter.setOnItemClickCallback(object : StoriesListAdapter.OnItemClickCallback {
                    override fun onItemClicked(user: ListStoryItem) {
                        val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
                        intentDetail.putExtra(DetailActivity.DETAIL_STORIES, user)
                        startActivity(intentDetail)
                    }
                })
            } else {
                showToast(getString(R.string.error_get_stories))
                showLoading(false)
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            mainBinding.progressBar.visibility = View.VISIBLE
        } else {
            mainBinding.progressBar.visibility = View.INVISIBLE
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