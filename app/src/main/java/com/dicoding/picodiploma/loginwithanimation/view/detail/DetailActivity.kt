package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val story = intent.getParcelableExtra<ListStoryItem>(STORY) as ListStoryItem

        Glide
            .with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(binding.tvDetailPhoto)
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
    }

    companion object {
        const val STORY = "story"
    }
}