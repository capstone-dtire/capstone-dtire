package com.dtire.dtireapp.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.Adapter
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.HistoryResponse
import com.dtire.dtireapp.databinding.ActivityHistoryBinding
import com.dtire.dtireapp.utils.StateCallback

class HistoryActivity : AppCompatActivity(), StateCallback<HistoryResponse> {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: Adapter
    private lateinit var preferences: UserPreference
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbHistory
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detection_history)

        historyAdapter = Adapter()

        preferences = UserPreference(this)
        val userId = preferences.getUserId()
        viewModel.getHistory(userId).observe(this) {
            when(it) {
                is State.Success -> it.data?.let { data -> onSuccess(data) }
                is State.Error -> onFailed(it.message)
                is State.Loading -> onLoading()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess(data: HistoryResponse) {
        historyAdapter.setAllData(data.detectionHistory)
        binding.rvHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    override fun onLoading() {

    }

    override fun onFailed(message: String?) {
        Log.d("TAGG", "onFailed: $message")
    }
}