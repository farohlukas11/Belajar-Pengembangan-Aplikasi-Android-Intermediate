package com.dicoding.storyappsub1.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.dicoding.storyappsub1.databinding.ActivityDetailBinding
import com.dicoding.storyappsub1.model.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private var detailStories: ListStoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        setupView()

        detailStories = intent.getParcelableExtra(DETAIL_STORIES)

        if (detailStories != null) {
            detailStories?.let { dataDetail ->
                setupDetail(dataDetail)
            }
        }
    }

    private fun setupDetail(item: ListStoryItem) {
        detailBinding.apply {
            Glide.with(this@DetailActivity).load(item.photoUrl).into(ivDetailPhoto)
            tvDetailName.text = item.name
            tvDetailDescription.text = item.description
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

    companion object {
        const val DETAIL_STORIES = "detail_stories"
    }
}