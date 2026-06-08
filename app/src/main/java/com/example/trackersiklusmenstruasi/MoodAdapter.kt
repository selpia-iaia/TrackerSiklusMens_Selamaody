package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemMoodBinding

class MoodAdapter(
    private val moods: List<MoodItem>,
    private val onMoodSelected: ((MoodItem) -> Unit)? = null
) : RecyclerView.Adapter<MoodAdapter.ViewHolder>() {

    private var selectedPosition = 3 // Default center item

    class ViewHolder(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mood = moods[position]
        holder.binding.tvEmoji.text = mood.emoji
        holder.binding.tvMoodName.text = mood.name

        if (position == selectedPosition) {
            // TERPILIH: Lebih besar, teks hitam, ada lingkaran pink di belakang emoji
            holder.binding.vEmojiBg.visibility = View.VISIBLE
            holder.binding.tvMoodName.setTextColor(Color.BLACK)
            holder.binding.tvMoodName.textSize = 24f
            holder.binding.root.alpha = 1.0f
            holder.binding.root.scaleX = 1.1f
            holder.binding.root.scaleY = 1.1f
        } else {
            // TIDAK TERPILIH: Lebih kecil, teks abu-abu, tanpa lingkaran
            holder.binding.vEmojiBg.visibility = View.GONE
            holder.binding.tvMoodName.setTextColor(Color.parseColor("#9CA3AF"))
            holder.binding.tvMoodName.textSize = 18f
            holder.binding.root.alpha = 0.5f
            holder.binding.root.scaleX = 0.9f
            holder.binding.root.scaleY = 0.9f
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onMoodSelected?.invoke(mood)
        }
    }

    fun setSelected(position: Int) {
        if (selectedPosition == position) return
        val oldPos = selectedPosition
        selectedPosition = position
        if (oldPos != -1) notifyItemChanged(oldPos)
        if (selectedPosition != -1) notifyItemChanged(selectedPosition)
    }

    fun getSelectedItem(): MoodItem = moods[selectedPosition]

    override fun getItemCount(): Int = moods.size
}
