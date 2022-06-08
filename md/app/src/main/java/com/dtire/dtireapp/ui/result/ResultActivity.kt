package com.dtire.dtireapp.ui.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.dtire.dtireapp.R
import com.dtire.dtireapp.databinding.ActivityResultBinding
import com.dtire.dtireapp.ui.home.HomeActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbResult
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val picturePath = intent.getStringExtra(EXTRA_IMAGE)
//        val result = BitmapFactory.decodeFile(picturePath)

//        binding.ivResultPicture.setImageBitmap(result)
//        binding.ivResultPicture.setOnClickListener {
//            val optionsCompat: ActivityOptionsCompat =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    this, binding.ivResultPicture, "detail_image"
//                )
//            val intent = Intent(this@ResultActivity, ImageDetailActivity::class.java)
//            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, picturePath)
//            startActivity(intent, optionsCompat.toBundle())
//        }

        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivResultPicture)

        val result = intent.getDoubleExtra(EXTRA_IMAGE_RESULT, 0.0)
        if (result <= 0.5) {
            binding.apply {
                tvResultTitle.text = getString(R.string.result)
                tvResultDetail.text = getString(R.string.result_crack)
            }
        } else {
            binding.apply {
                tvResultTitle.text = getString(R.string.result)
                tvResultDetail.text = getString(R.string.result_ok)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val origin = intent.getStringExtra(EXTRA_ORIGIN)
                if (origin == "home" ) {
                    val intent = Intent(this@ResultActivity, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    finish()
                }
            }
            else -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_IMAGE_RESULT = "extra_image_result"
        const val EXTRA_ORIGIN = "extra_origin"
    }
}