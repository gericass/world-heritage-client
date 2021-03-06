package com.github.gericass.world_heritage_client.library.playlist.show

import com.github.gericass.world_heritage_client.data.AvgleRepository
import com.github.gericass.world_heritage_client.data.PlaylistId
import com.github.gericass.world_heritage_client.data.model.*
import com.github.gericass.world_heritage_client.data.remote.PagingManager

class PlaylistUseCase(
    private val repository: AvgleRepository
) {

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> getVideosByPlaylistId(
        playlistId: Int,
        pagingManager: PagingManager<T>
    ): List<Videos.Video> {
        return when (playlistId) {
            PlaylistId.FAVORITE.id -> {
                repository.getFavoriteVideos(
                    pagingManager = (pagingManager as PagingManager<FavoriteVideo>)
                ).map(FavoriteVideo::toVideo)
            }
            PlaylistId.LATER.id -> {
                repository.getLaterVideos(
                    pagingManager = (pagingManager as PagingManager<LaterVideo>)
                ).map(LaterVideo::toVideo)
            }
            else -> {
                repository.getPlaylistWithVideos(playlistId).videos.map(VideoEntity::toVideo)
            }
        }
    }

    suspend fun getPlaylist(playlistId: Int): Playlist {
        return when (playlistId) {
            PlaylistId.FAVORITE.id -> Playlist(playlistId, PlaylistId.FAVORITE.title)
            PlaylistId.LATER.id -> Playlist(playlistId, PlaylistId.LATER.title)
            else -> repository.getPlaylistWithVideos(playlistId).playlist
        }
    }

    suspend fun deleteVideo(playlistId: Int, video: Videos.Video) {
        when (playlistId) {
            PlaylistId.FAVORITE.id -> repository.deleteFavoriteVideo(video.vid)
            PlaylistId.LATER.id -> repository.deleteLaterVideo(video.vid)
            else -> repository.deleteVideoFromPlaylist(playlistId, video)

        }
    }

    fun getPagingManagerByPlaylistId(playlistId: Int): PagingManager<*> {
        return when (playlistId) {
            PlaylistId.FAVORITE.id -> PagingManager(FavoriteVideo::class)
            PlaylistId.LATER.id -> PagingManager(LaterVideo::class)
            else -> PagingManager(Videos.Video::class)
        }
    }
}