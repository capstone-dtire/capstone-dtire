package com.dtire.dtireapp.ui.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.HistorySuccessResponse
import com.dtire.dtireapp.databinding.ActivityResultBinding
import com.dtire.dtireapp.ui.home.HomeActivity
import com.dtire.dtireapp.utils.StateCallback

class ResultActivity : AppCompatActivity(), StateCallback<HistorySuccessResponse> {
    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels()
    private var condition: String = ""
    private var recommendation: String = ""
    private lateinit var preferences: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbResult
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivResultPicture)

        val result = intent.getDoubleExtra(EXTRA_IMAGE_RESULT, 0.0)
        if (result <= 0.5) {
            binding.apply {
                condition = "crack"
                recommendation = getString(R.string.result_crack)
                tvResultTitle.text = getString(R.string.result)
                tvResultDetail.text = getString(R.string.result_crack)
            }
        } else {
            binding.apply {
                condition = "ok"
                recommendation = getString(R.string.result_ok)
                tvResultTitle.text = getString(R.string.result)
                tvResultDetail.text = getString(R.string.result_ok)
            }
        }

        preferences = UserPreference(this)
        val id = preferences.getUserId()
        if (imageUrl != null) {
            viewModel.addToHistory(id, condition, recommendation, imageUrl).observe(this) {
                when(it) {
                    is State.Success -> it.data?.let { data -> onSuccess(data) }
                    is State.Loading -> onLoading()
                    is State.Error -> onFailed(it.message)
                }
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

    override fun onSuccess(data: HistorySuccessResponse) {

    }

    override fun onLoading() {

    }

    override fun onFailed(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_IMAGE_RESULT = "extra_image_result"
        const val EXTRA_ORIGIN = "extra_origin"
    }
}