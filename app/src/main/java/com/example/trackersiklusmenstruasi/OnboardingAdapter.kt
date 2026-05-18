package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val list: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOnboardingBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.image.setImageResource(item.image)
        
        if (item.secondaryImage != null) {
            holder.binding.imageSecondary.visibility = android.view.View.VISIBLE
            holder.binding.imageSecondary.setImageResource(item.secondaryImage)
        } else {
            holder.binding.imageSecondary.visibility = android.view.View.GONE
        }

        if (item.tertiaryImage != null) {
            holder.binding.imageTertiary.visibility = android.view.View.VISIBLE
            holder.binding.imageTertiary.setImageResource(item.tertiaryImage)
            
            // Adjust transparency for calendar if it's the calendar icon
            if (item.title.contains("Pelacakan")) {
                holder.binding.imageTertiary.alpha = 1.0f
                holder.binding.flowerSmall.alpha = 0.4f
            } else {
                holder.binding.imageTertiary.alpha = 0.6f
            }
            
            holder.binding.flowerSmall.visibility = android.view.View.VISIBLE
            holder.binding.flowerSmall.setImageResource(item.secondaryImage ?: 0)
        } else {
            holder.binding.imageTertiary.visibility = android.view.View.GONE
            holder.binding.flowerSmall.visibility = android.view.View.GONE
        }

        holder.binding.title.text = item.title
        holder.binding.desc.text = item.desc
    }
}