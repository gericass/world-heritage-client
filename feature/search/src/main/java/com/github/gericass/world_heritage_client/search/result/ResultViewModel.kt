package com.github.gericass.world_heritage_client.search.result

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.gericass.world_heritage_client.common.vo.Status
import com.github.gericass.world_heritage_client.data.AvgleRepository
import com.github.gericass.world_heritage_client.data.model.Videos

class ResultViewModel(
    repository: AvgleRepository
) : ViewModel(), LifecycleObserver {


    val keyword = MutableLiveData<String>()
    val keywordClick = MutableLiveData<Unit>()

    val pagedList: LiveData<PagedList<Videos.Video>>

    private val _networkStatus = MutableLiveData<Status>()
    val networkStatus: LiveData<Status> = _networkStatus

    private val factory = ResultDataSourceFactory(viewModelScope, repository, _networkStatus)

    init {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(50).build()
        pagedList = LivePagedListBuilder(factory, pagedListConfig)
            .build()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        fetch()
    }

    private fun fetch() {
        keyword.value?.let {
            factory.refresh(it)
        }
    }

    fun onKeywordClick() {
        keywordClick.value = Unit
    }
}