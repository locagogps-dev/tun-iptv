package com.tuniptv.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuniptv.app.R

/**
 * Horizontal category chips ("All", "Favorites", then one chip per
 * group-title found in the playlist). The selected chip is highlighted
 * by the state_selected background selector.
 */
class CategoryAdapter(
    private var categories: List<String>,
    private var selected: String,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    fun update(categories: List<String>, selected: String) {
        this.categories = categories
        this.selected = selected
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val label: TextView = view.findViewById(R.id.category_label)

        fun bind(category: String) {
            label.text = category
            itemView.isSelected = category == selected
            itemView.setOnClickListener { onCategorySelected(category) }
        }
    }
}
