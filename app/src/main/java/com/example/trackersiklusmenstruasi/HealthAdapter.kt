package com.example.trackersiklusmenstruasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ItemHealthArticleBinding

class HealthAdapter : ListAdapter<HealthArticle, HealthAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHealthArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHealthArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: HealthArticle) {
            binding.tvTitle.text = article.title
            binding.tvContent.text = article.content
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HealthArticle>() {
        override fun areItemsTheSame(oldItem: HealthArticle, newItem: HealthArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HealthArticle, newItem: HealthArticle): Boolean {
            return oldItem == newItem
        }
    }
}
