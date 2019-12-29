package com.github.gericass.world_heritage_client.library.playlist

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.github.gericass.world_heritage_client.common.CommonViewVideoSmallBindingModel_
import com.github.gericass.world_heritage_client.common.view.VideoClickListener
import com.github.gericass.world_heritage_client.common.view.progressView
import com.github.gericass.world_heritage_client.data.model.Videos
import com.github.gericass.world_heritage_client.library.LibraryViewPlaylistHeaderBindingModel_

class PlaylistController(
    private val title: String,
    private val description: String,
    private val videoClickListener: VideoClickListener,
    private val editButtonListener: EditButtonListener,
    private val editable: Boolean
) : PagedListEpoxyController<Videos.Video>() {

    var isLoading = true
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildItemModel(currentPosition: Int, item: Videos.Video?): EpoxyModel<*> {
        return CommonViewVideoSmallBindingModel_().apply {
            id(currentPosition)
            item?.let {
                video(it)
            }
            draggable(true)
            listener(videoClickListener)
        }
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        LibraryViewPlaylistHeaderBindingModel_().apply {
            id("header")
            title(title)
            description(description)
            editable(editable)
            listener(editButtonListener)
        }.addTo(this)
        super.addModels(models)
        if (isLoading) {
            progressView {
                id("progress")
            }
        }
    }

    interface EditButtonListener {
        fun onEditButtonClick()
    }
}