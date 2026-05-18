package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemCupPickerBinding

<<<<<<< HEAD
class CupAdapter(
    private val items: List<String>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<CupAdapter.ViewHolder>() {

    private var selectedPosition = 2 // Default middle item
=======
class CupAdapter(private val items: List<String>) : RecyclerView.Adapter<CupAdapter.ViewHolder>() {
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba

    class ViewHolder(val binding: ItemCupPickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCupPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvVolume.text = items[position]
<<<<<<< HEAD
        
        // Normalize font for all items: Gray color and consistent size
        holder.binding.tvVolume.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
        holder.binding.tvVolume.textSize = 20f
        
        if (position == selectedPosition) {
            // SELECTED: Only highlight via icon
            holder.binding.ivCup.alpha = 1.0f
            holder.binding.ivCup.scaleX = 1.1f
            holder.binding.ivCup.scaleY = 1.1f
        } else {
            // NOT SELECTED: Faded icon
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

=======
        // logic for scaling/color can be added here or in onScroll
    }

>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
    override fun getItemCount(): Int = items.size
}
