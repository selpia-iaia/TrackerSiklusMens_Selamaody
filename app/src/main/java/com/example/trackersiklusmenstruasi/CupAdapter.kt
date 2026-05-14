package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemCupPickerBinding

class CupAdapter(private val items: List<String>) : RecyclerView.Adapter<CupAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCupPickerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCupPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvVolume.text = items[position]
        // logic for scaling/color can be added here or in onScroll
    }

    override fun getItemCount(): Int = items.size
}
