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
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyappsub1.R
import com.dicoding.storyappsub1.camera.MainCamera
import com.dicoding.storyappsub1.databinding.ActivityMainBinding
import com.dicoding.storyappsub1.detail.DetailActivity
import com.dicoding.storyappsub1.model.ListStoriesAdapter
import com.dicoding.storyappsub1.model.ListStoryItem
import com.dicoding.storyappsub1.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setupView()

        mainViewModel.mediator.observe(this) {}
        mainViewModel.user.observe(this) { tokenUser ->
            mainViewModel.getStories(tokenUser)
        }

        mainViewModel.isRegistred.observe(this) {
            if (it == false) {
                mainViewModel.listStory.observe(this) { list ->
                    if (list != null) {
                        showRecyclerView(list)
                        showLoading(false)
                    }
                }
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.messageRegistred.observe(this) {
            if (it == null) {
                showToast(getString(R.string.story_error))
            } else {
                showToast(it)
            }
        }

        mainBinding.fab.setOnClickListener {
            startActivity(Intent(this, MainCamera::class.java))
        }

        mainBinding.fabSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun showRecyclerView(list: List<ListStoryItem>) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainBinding.rvStories.layoutManager = GridLayoutManager(this, 2)
        } else {
            mainBinding.rvStories.layoutManager = LinearLayoutManager(this)
        }

        val listStories = ArrayList<ListStoryItem>()
        for (stories in list) {
            listStories.add(stories)
        }
        val listStoriesAdapter = ListStoriesAdapter(listStories, this)
        mainBinding.rvStories.adapter = listStoriesAdapter

        listStoriesAdapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(user: ListStoryItem) {
                val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.DETAIL_STORIES, user)
                startActivity(intentDetail)
            }
        })
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