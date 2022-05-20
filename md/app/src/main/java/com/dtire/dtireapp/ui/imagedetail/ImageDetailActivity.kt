package com.dtire.dtireapp.ui.imagedetail

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dtire.dtireapp.databinding.ActivityImageDetailBinding
import com.dtire.dtireapp.databinding.ActivityResultBinding
import com.dtire.dtireapp.ui.result.ResultActivity

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val picturePath = intent.getStringExtra(ResultActivity.EXTRA_IMAGE)
        val result = BitmapFactory.decodeFile(picturePath)
        binding.ivImageDetail.setImageBitmap(result)
    }
    companion object {
        const val EXTRA_IMAGE = "extra_image"
    }
}