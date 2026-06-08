package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemCupPickerBinding

class CupAdapter(
    private val items: List<String>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<CupAdapter.ViewHolder>() {

    private var selectedPosition = 2 

    class ViewHolder(val binding: ItemCupPickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCupPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvVolume.text = items[position]
        
        if (position == selectedPosition) {
            // TERPILIH: Sangat besar, teks hitam tebal
            holder.binding.tvVolume.setTextColor(Color.BLACK)
            holder.binding.tvVolume.textSize = 34f
            holder.binding.ivCup.alpha = 1.0f
            holder.binding.ivCup.scaleX = 1.4f
            holder.binding.ivCup.scaleY = 1.4f
        } else {
            // TIDAK TERPILIH: Lebih kecil dan samar
            holder.binding.tvVolume.setTextColor(Color.parseColor("#D1D5DB"))
            holder.binding.tvVolume.textSize = 20f
            holder.binding.ivCup.alpha = 0.3f
            holder.binding.ivCup.scaleX = 0.9f
            holder.binding.ivCup.scaleY = 0.9f
        }
        
        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onItemSelected(items[position])
        }
    }

    fun getSelectedItem(): String = items[selectedPosition]

    override fun getItemCount(): Int = items.size
}
