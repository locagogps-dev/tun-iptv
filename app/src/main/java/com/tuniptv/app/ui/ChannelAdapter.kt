package com.tuniptv.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tuniptv.app.R
import com.tuniptv.app.model.Channel

/**
 * Vertical channel list. Each row shows the logo (Coil), the name,
 * the group, and a star button to toggle the favorite state.
 */
class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit,
    private val onFavoriteClick: (Channel) -> Unit,
    private val isFavorite: (Channel) -> Boolean
) : ListAdapter<Channel, ChannelAdapter.ChannelViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logo: ImageView = view.findViewById(R.id.channel_logo)
        private val name: TextView = view.findViewById(R.id.channel_name)
        private val group: TextView = view.findViewById(R.id.channel_group)
        private val favorite: ImageButton = view.findViewById(R.id.channel_favorite)

        fun bind(channel: Channel) {
            name.text = channel.name
            group.text = channel.group
            logo.load(channel.logoUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_tv_placeholder)
                error(R.drawable.ic_tv_placeholder)
            }
            val fav = isFavorite(channel)
            favorite.setImageResource(
                if (fav) R.drawable.ic_star else R.drawable.ic_star_outline
            )
            favorite.contentDescription = if (fav) {
                itemView.context.getString(R.string.remove_from_favorites)
            } else {
                itemView.context.getString(R.string.add_to_favorites)
            }
            itemView.setOnClickListener { onChannelClick(channel) }
            favorite.setOnClickListener {
                onFavoriteClick(channel)
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean =
                oldItem.id == newItem.id && oldItem.streamUrl == newItem.streamUrl

            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean =
                oldItem == newItem
        }
    }
}
