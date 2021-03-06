package com.github.gericass.world_heritage_client.home.category

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.github.gericass.world_heritage_client.common.vo.Status
import com.github.gericass.world_heritage_client.data.AvgleRepository
import com.github.gericass.world_heritage_client.data.model.Videos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryDataSource(
    private val scope: CoroutineScope,
    private val repository: AvgleRepository,
    private val networkState: MutableLiveData<Status>,
    var categoryId: String? = null
) : PageKeyedDataSource<Int, Videos.Video>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Videos.Video>
    ) {
        scope.launch {
            fetch(0) { page, collections ->
                callback.onResult(collections, null, page)
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Videos.Video>
    ) {
        scope.launch {
            fetch(params.key) { page, collections ->
                callback.onResult(collections, page)
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Videos.Video>
    ) {
        // do nothing
    }

    private suspend fun fetch(
        page: Int,
        callback: (Int?, List<Videos.Video>) -> Unit
    ) {
        val id = categoryId ?: return
        try {
            networkState.postValue(Status.LOADING)
            val collections = repository.getVideoByCategory(page, id)
            if (collections.response.has_more) {
                val next = page + 1
                callback(next, collections.response.videos)
            } else {
                callback(null, collections.response.videos)
            }
            networkState.postValue(Status.SUCCESS)
        } catch (e: Throwable) {
            Timber.e(e)
            networkState.postValue(Status.ERROR)
        }
    }
}