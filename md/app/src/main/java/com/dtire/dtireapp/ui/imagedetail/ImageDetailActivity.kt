package com.dtire.dtireapp.ui.imagedetail

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.dtire.dtireapp.databinding.ActivityImageDetailBinding
import com.dtire.dtireapp.ui.result.ResultActivity

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbImageDetail
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val picturePath = intent.getStringExtra(ResultActivity.EXTRA_IMAGE)
        val result = BitmapFactory.decodeFile(picturePath)
        binding.ivImageDetail.setImageBitmap(result)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
    }
}