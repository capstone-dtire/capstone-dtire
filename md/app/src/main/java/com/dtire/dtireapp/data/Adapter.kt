package com.dtire.dtireapp.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtire.dtireapp.data.response.DetectionHistoryItem
import com.dtire.dtireapp.databinding.HistoryItemBinding
import com.dtire.dtireapp.ui.result.ResultActivity

class Adapter: RecyclerView.Adapter<Adapter.AdapterViewHolder>() {
    private val listHistory = ArrayList<DetectionHistoryItem?>()

    fun setAllData(data: List<DetectionHistoryItem>) {
        val reversedData = data.reversed()
        listHistory.apply {
            clear()
            addAll(reversedData)
        }
    }

    class AdapterViewHolder(private var binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: DetectionHistoryItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(history.imageUrl)
                    .circleCrop()
                    .into(ivHistoryItem)
                tvHistoryItemTitle.text = history.conditionTitle
                tvHistoryItemDate.text = history.dateOfCheck
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_IMAGE_URL, history.imageUrl.toString())
                intent.putExtra(ResultActivity.EXTRA_IMAGE_RESULT, history.conditionTitle)
                intent.putExtra(ResultActivity.EXTRA_IMAGE_DATE, history.dateOfCheck)
                intent.putExtra(ResultActivity.EXTRA_ORIGIN, "history")
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        listHistory[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = listHistory.size

}