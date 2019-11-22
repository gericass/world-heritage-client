package com.github.gericass.world_heritage_client.library

import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import com.github.gericass.world_heritage_client.common.view.VideoClickListener
import com.github.gericass.world_heritage_client.common.view.subjectTextView
import com.github.gericass.world_heritage_client.data.model.ViewingHistory


class LibraryController(
    private val videoClickListener: VideoClickListener
) : TypedEpoxyController<List<ViewingHistory>>() {

    override fun buildModels(data: List<ViewingHistory>?) {
        subjectTextView {
            id("recent_contents")
            text("最近視聴したコンテンツ")
            withPaddingStyle()
            sectionStyle(true)
        }
        val videos = data?.map {
            LibraryViewVideoBindingModel_().apply {
                id(it.vid)
                video(it.toVideo())
                listener(videoClickListener)
            }
        } ?: return
        carousel {
            id("history")
            models(videos)
        }
    }

}