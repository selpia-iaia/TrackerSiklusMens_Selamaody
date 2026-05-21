package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemMoodBinding

data class MoodItem(val name: String, val emoji: String)

class MoodAdapter(private val moods: List<MoodItem>) : RecyclerView.Adapter<MoodAdapter.ViewHolder>() {

    private var selectedPosition = -1

    fun setSelected(position: Int) {
        val old = selectedPosition
        selectedPosition = position
        notifyItemChanged(old)
        notifyItemChanged(selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mood = moods[position % moods.size]
        holder.binding.tvMoodName.text = mood.name
        holder.binding.tvEmoji.text = mood.emoji

        if (position == selectedPosition) {
            holder.binding.iconContainer.visibility = View.VISIBLE
            holder.binding.tvMoodName.textSize = 22f
            holder.binding.tvMoodName.alpha = 1.0f
            holder.binding.tvMoodName.setTextColor(Color.BLACK)
        } else {
            holder.binding.iconContainer.visibility = View.GONE
            holder.binding.tvMoodName.textSize = 16f
            holder.binding.tvMoodName.alpha = 0.4f
            holder.binding.tvMoodName.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = moods.size

    class ViewHolder(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)
}
