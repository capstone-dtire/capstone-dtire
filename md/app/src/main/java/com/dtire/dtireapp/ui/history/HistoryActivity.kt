package com.dtire.dtireapp.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.dtire.dtireapp.data.Adapter
import com.dtire.dtireapp.data.entity.History
import com.dtire.dtireapp.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbHistory
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        historyAdapter = Adapter()

        val dummyData: List<History> = listOf(
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
            History(photoUrl = "https://avatars.githubusercontent.com/u/8178172?v=4", title = "title", date = "4"),
        )
        historyAdapter.setAllData(dummyData)

        binding.rvHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}