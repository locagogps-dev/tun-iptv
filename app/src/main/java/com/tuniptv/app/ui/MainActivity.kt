package com.tuniptv.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuniptv.app.R
import com.tuniptv.app.data.DEFAULT_PLAYLISTS
import com.tuniptv.app.data.FavoritesManager
import com.tuniptv.app.data.PlaylistSource
import com.tuniptv.app.data.Session
import com.tuniptv.app.model.Channel
import kotlinx.coroutines.launch

/**
 * Phone home screen: playlist selector, search box, horizontal category
 * chips, vertical channel list with logos and favorites, loading / empty /
 * error states. Tapping a channel opens [PlayerActivity].
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var favoritesManager: FavoritesManager

    private lateinit var playlistSelector: Spinner
    private lateinit var searchBox: EditText
    private lateinit var categoryList: RecyclerView
    private lateinit var channelList: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorLayout: View
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var addPlaylistButton: Button
    private lateinit var emptyView: TextView
    private lateinit var channelCount: TextView

    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var allLabel: String
    private lateinit var favoritesLabel: String

    private var allChannels: List<Channel> = emptyList()
    private var displayedChannels: List<Channel> = emptyList()
    private var selectedCategory: String = ""
    private var searchQuery: String = ""
    private var playlistSources: List<PlaylistSource> = DEFAULT_PLAYLISTS
    private var spinnerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        favoritesManager = FavoritesManager(this)
        allLabel = getString(R.string.all)
        favoritesLabel = getString(R.string.favorites)
        selectedCategory = allLabel

        playlistSelector = findViewById(R.id.playlist_selector)
        searchBox = findViewById(R.id.search_box)
        categoryList = findViewById(R.id.category_list)
        channelList = findViewById(R.id.channel_list)
        loadingIndicator = findViewById(R.id.loading_indicator)
        errorLayout = findViewById(R.id.error_layout)
        errorText = findViewById(R.id.error_text)
        retryButton = findViewById(R.id.retry_button)
        addPlaylistButton = findViewById(R.id.add_playlist_button)
        emptyView = findViewById(R.id.empty_view)
        channelCount = findViewById(R.id.channel_count)

        channelAdapter = ChannelAdapter(
            onChannelClick = { channel -> openPlayer(channel) },
            onFavoriteClick = { channel -> favoritesManager.toggleFavorite(channel.id) },
            isFavorite = { channel -> favoritesManager.isFavorite(channel.id) }
        )
        channelList.layoutManager = LinearLayoutManager(this)
        channelList.adapter = channelAdapter

        categoryAdapter = CategoryAdapter(emptyList(), selectedCategory) { category ->
            selectedCategory = category
            applyFilters()
        }
        categoryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryList.adapter = categoryAdapter

        setupPlaylistSelector()
        setupSearch()
        retryButton.setOnClickListener { viewModel.load() }
        addPlaylistButton.setOnClickListener { showAddPlaylistDialog() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is PlaylistUiState.Loading -> showLoading()
                        is PlaylistUiState.Success -> {
                            allChannels = state.channels
                            selectedCategory = allLabel
                            searchQuery = ""
                            searchBox.setText("")
                            updateCategories()
                            applyFilters()
                            showContent()
                        }
                        is PlaylistUiState.Error -> showError(state.message)
                    }
                }
            }
        }

        val initialUrl = favoritesManager.lastPlaylistUrl()
            .takeIf { it.isNotBlank() && playlistSources.any { s -> s.url == it } }
            ?: DEFAULT_PLAYLISTS.first().url
        viewModel.load(initialUrl)
    }

    override fun onResume() {
        super.onResume()
        // Favorites may have changed while the player was open.
        channelAdapter.notifyDataSetChanged()
        applyFilters()
    }

    private fun setupPlaylistSelector() {
        playlistSources = buildPlaylistSources()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            playlistSources.map { it.label }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        playlistSelector.adapter = adapter
        val lastUrl = favoritesManager.lastPlaylistUrl()
        val initialPosition = playlistSources.indexOfFirst { it.url == lastUrl }
            .takeIf { it >= 0 } ?: 0
        playlistSelector.setSelection(initialPosition, false)
        playlistSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!spinnerReady) {
                    spinnerReady = true
                    return
                }
                val url = playlistSources[position].url
                favoritesManager.setLastPlaylistUrl(url)
                viewModel.load(url)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun buildPlaylistSources(): List<PlaylistSource> {
        val custom = favoritesManager.customPlaylistUrl()
        return if (custom.isNotBlank()) {
            DEFAULT_PLAYLISTS + PlaylistSource("Custom", custom)
        } else {
            DEFAULT_PLAYLISTS
        }
    }

    private fun setupSearch() {
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString().orEmpty()
                applyFilters()
            }
        })
    }

    private fun showAddPlaylistDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_TEXT_VARIATION_URI
            hint = getString(R.string.custom_playlist_hint)
            setText(favoritesManager.customPlaylistUrl())
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.custom_playlist_title)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val url = input.text.toString().trim()
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    favoritesManager.setCustomPlaylistUrl(url)
                    favoritesManager.setLastPlaylistUrl(url)
                    // The selection change below fires onItemSelected once,
                    // which triggers the single load of the new URL.
                    spinnerReady = false
                    setupPlaylistSelector()
                    playlistSelector.setSelection(playlistSources.size - 1)
                } else {
                    Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun categories(): List<String> {
        val groups = allChannels.map { it.group }.distinct().sorted()
        return listOf(allLabel, favoritesLabel) + groups
    }

    private fun updateCategories() {
        categoryAdapter.update(categories(), selectedCategory)
    }

    private fun applyFilters() {
        val query = searchQuery.trim().lowercase()
        displayedChannels = allChannels.filter { channel ->
            val matchesCategory = when (selectedCategory) {
                allLabel -> true
                favoritesLabel -> favoritesManager.isFavorite(channel.id)
                else -> channel.group == selectedCategory
            }
            val matchesQuery = query.isEmpty() ||
                channel.name.lowercase().contains(query) ||
                channel.group.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
        channelAdapter.submitList(displayedChannels)
        channelCount.text = getString(R.string.channels_count, displayedChannels.size)
        emptyView.visibility = if (displayedChannels.isEmpty()) View.VISIBLE else View.GONE
        categoryAdapter.update(categories(), selectedCategory)
    }

    private fun openPlayer(channel: Channel) {
        Session.channels = displayedChannels
        Session.currentIndex = displayedChannels.indexOf(channel).coerceAtLeast(0)
        startActivity(Intent(this, PlayerActivity::class.java))
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        channelList.visibility = View.GONE
        errorLayout.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    private fun showContent() {
        loadingIndicator.visibility = View.GONE
        channelList.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        loadingIndicator.visibility = View.GONE
        channelList.visibility = View.GONE
        emptyView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = getString(R.string.playlist_load_error) + "\n" + message
    }
}
