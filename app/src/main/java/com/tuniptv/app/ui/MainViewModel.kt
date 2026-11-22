package com.tuniptv.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuniptv.app.data.DEFAULT_PLAYLISTS
import com.tuniptv.app.data.PlaylistRepository
import com.tuniptv.app.data.Session
import com.tuniptv.app.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Success(val channels: List<Channel>) : PlaylistUiState
    data class Error(val message: String) : PlaylistUiState
}

/** Loads a playlist in the background and exposes loading / success / error. */
class MainViewModel(
    private val repository: PlaylistRepository = PlaylistRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val state: StateFlow<PlaylistUiState> = _state.asStateFlow()

    var currentUrl: String = DEFAULT_PLAYLISTS.first().url
        private set

    fun load(url: String = currentUrl) {
        currentUrl = url
        _state.value = PlaylistUiState.Loading
        viewModelScope.launch {
            try {
                val channels = repository.load(url)
                Session.channels = channels
                _state.value = PlaylistUiState.Success(channels)
            } catch (e: Exception) {
                _state.value = PlaylistUiState.Error(
                    e.message ?: "Failed to load playlist"
                )
            }
        }
    }
}
