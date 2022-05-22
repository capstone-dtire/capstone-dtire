package com.dtire.dtireapp.ui.result

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.dtire.dtireapp.databinding.ActivityResultBinding
import com.dtire.dtireapp.ui.imagedetail.ImageDetailActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbResult
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val picturePath = intent.getStringExtra(EXTRA_IMAGE)
        val result = BitmapFactory.decodeFile(picturePath)

        binding.ivResultPicture.setImageBitmap(result)
        binding.ivResultPicture.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, binding.ivResultPicture, "detail_image"
                )
            val intent = Intent(this@ResultActivity, ImageDetailActivity::class.java)
            intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, picturePath)
            startActivity(intent, optionsCompat.toBundle())
        }

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