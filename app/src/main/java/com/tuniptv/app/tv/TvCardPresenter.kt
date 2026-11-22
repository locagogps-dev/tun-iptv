package com.tuniptv.app.tv

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import com.tuniptv.app.R
import com.tuniptv.app.model.Channel

/**
 * Leanback card: channel logo (Coil), name as title, group as content.
 * Cards are focusable and D-pad navigable by the Leanback framework.
 */
class TvCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context)
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        cardView.titleText = channel.name
        cardView.contentText = channel.group
        cardView.mainImageView.load(channel.logoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_tv_placeholder)
            error(R.drawable.ic_tv_placeholder)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImageView.setImageDrawable(null)
    }

    companion object {
        private const val CARD_WIDTH = 320
        private const val CARD_HEIGHT = 180
    }
}
