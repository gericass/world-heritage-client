package com.github.gericass.world_heritage_client.library

import androidx.lifecycle.*
import com.github.gericass.world_heritage_client.common.vo.Status
import com.github.gericass.world_heritage_client.data.AvgleRepository
import com.github.gericass.world_heritage_client.data.model.Playlist
import com.github.gericass.world_heritage_client.data.model.ViewingHistory
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: AvgleRepository
) : ViewModel(), LifecycleObserver {

    private val _history = MutableLiveData<List<ViewingHistory>>()
    val history: LiveData<List<ViewingHistory>> = _history

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    val isRefreshing = MutableLiveData<Boolean>()

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchHistories() {
        viewModelScope.launch {
            _loadingStatus.value = Status.LOADING
            try {
                _history.value = repository.getViewingHistories(15)
                _playlists.value = repository.getAllPlaylist()
                _loadingStatus.value = Status.SUCCESS
            } catch (e: Throwable) {
                _loadingStatus.value = Status.ERROR
            }
        }
    }

    fun refresh() {
        fetchHistories()
    }
}