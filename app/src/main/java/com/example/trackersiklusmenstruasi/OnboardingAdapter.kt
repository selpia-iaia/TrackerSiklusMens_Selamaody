package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val list: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        with(holder.binding) {
            mainImage.setImageResource(item.image)
            
            // Handle Secondary Image visibility and content
            if (item.secondaryImage != null) {
                secondaryImage.visibility = View.VISIBLE
                secondaryImage.setImageResource(item.secondaryImage)
            } else {
                secondaryImage.visibility = View.GONE
            }

            // Handle Tertiary Image visibility and content
            if (item.tertiaryImage != null) {
                tertiaryImage.visibility = View.VISIBLE
                tertiaryImage.setImageResource(item.tertiaryImage)
            } else {
                tertiaryImage.visibility = View.GONE
            }

            title.text = item.title
            desc.text = item.desc
        }
    }
}
