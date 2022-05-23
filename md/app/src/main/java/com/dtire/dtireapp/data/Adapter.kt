package com.dtire.dtireapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtire.dtireapp.data.entity.History
import com.dtire.dtireapp.databinding.HistoryItemBinding

class Adapter: RecyclerView.Adapter<Adapter.AdapterViewHolder>() {
    private val listHistory = ArrayList<History>()

    fun setAllData(data: List<History>) {
        listHistory.apply {
            clear()
            addAll(data)
        }
    }

    class AdapterViewHolder(private var binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(history.photoUrl)
                    .circleCrop()
                    .into(ivHistoryItem)
                tvHistoryItemTitle.text = history.title
                tvHistoryItemDate.text = history.date
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int = listHistory.size

}