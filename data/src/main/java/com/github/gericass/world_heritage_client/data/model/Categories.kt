package com.github.gericass.world_heritage_client.data.model

data class Categories(
    val response: Response,
    val success: Boolean
) {

    data class Response(
        val categories: List<Category>
    )

    data class Category(
        val CHID: String,
        val category_url: String,
        val cover_url: String,
        val name: String,
        val slug: String,
        val total_videos: Int
    )
}