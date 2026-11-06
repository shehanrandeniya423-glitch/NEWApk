package com.cookieeditor.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cookieeditor.app.databinding.ItemCookieBinding

class CookieAdapter(
    private val onEdit: (CookieItem) -> Unit,
    private val onDelete: (CookieItem) -> Unit
) : ListAdapter<CookieItem, CookieAdapter.CookieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookieViewHolder {
        val binding = ItemCookieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CookieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CookieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CookieViewHolder(
        private val binding: ItemCookieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cookie: CookieItem) {
            binding.tvName.text = cookie.name
            binding.tvValue.text = cookie.value
            binding.tvMeta.text = "Domain: ${cookie.domain}  |  Path: ${cookie.path}"

            binding.btnEdit.setOnClickListener { onEdit(cookie) }
            binding.btnDelete.setOnClickListener { onDelete(cookie) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CookieItem>() {
        override fun areItemsTheSame(oldItem: CookieItem, newItem: CookieItem): Boolean {
            return oldItem.name == newItem.name && oldItem.domain == newItem.domain
        }

        override fun areContentsTheSame(oldItem: CookieItem, newItem: CookieItem): Boolean {
            return oldItem == newItem
        }
    }
}
