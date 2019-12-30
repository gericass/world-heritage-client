package com.github.gericass.world_heritage_client.library


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.github.gericass.world_heritage_client.common.BaseFragment
import com.github.gericass.world_heritage_client.common.navigator.AvgleNavigator
import com.github.gericass.world_heritage_client.common.observe
import com.github.gericass.world_heritage_client.common.vo.SpinnerItem
import com.github.gericass.world_heritage_client.data.PlaylistId
import com.github.gericass.world_heritage_client.data.model.Playlist
import com.github.gericass.world_heritage_client.data.model.ViewingHistory
import com.github.gericass.world_heritage_client.library.databinding.LibraryFragmentLibraryBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class LibraryFragment : BaseFragment() {

    private val viewModel: LibraryViewModel by viewModel()

    private lateinit var binding: LibraryFragmentLibraryBinding

    override val recyclerView: EpoxyRecyclerView by lazy { binding.recycler }

    private val navigator: AvgleNavigator.LibraryNavigator by inject()

    private val playlistClickListener = { playlist: Playlist ->
        when (playlist.id) {
            PlaylistId.HISTORY.id -> {
                navigator.run {
                    findNavController().navigateToHistory()
                }
            }
            PlaylistId.FAVORITE.id -> {
                navigator.run {
                    findNavController().navigateToFavorite(
                        playlist.id,
                        playlist.title,
                        playlist.description
                    )
                }
            }
            PlaylistId.LATER.id -> {
                navigator.run {
                    findNavController().navigateToLater(
                        playlist.id,
                        playlist.title,
                        playlist.description
                    )
                }
            }
            else -> {
                navigator.run {
                    findNavController().navigateToPlaylist(
                        playlist.id,
                        playlist.title,
                        playlist.description
                    )
                }
            }
        }
    }

    private val libraryController by lazy {
        LibraryController(
            videoClickListener,
            playlistClickListener,
            {
                navigator.run {
                    requireActivity().navigateToNewPlaylist()
                }
            },
            listOf(
                SpinnerItem(getString(R.string.common_spinner_watch_later), ::saveVideoToLater),
                SpinnerItem(getString(R.string.common_spinner_playlist), ::showPlaylistDialog)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.run {
            observe(history, ::observeHistories)
            observe(playlists, ::observePlaylists)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.library_fragment_library, container, false)
        binding = LibraryFragmentLibraryBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)
        binding.apply {
            viewModel = this@LibraryFragment.viewModel
            lifecycleOwner = this@LibraryFragment
            recycler.setController(libraryController)
            refresh.setOnRefreshListener {
                this@LibraryFragment.viewModel.isRefreshing.value = true
                this@LibraryFragment.viewModel.refresh()
            }
        }
    }

    private fun observeHistories(histories: List<ViewingHistory>?) {
        libraryController.history = histories
    }

    private fun observePlaylists(playlists: List<Playlist>?) {
        val history = Playlist(
            PlaylistId.HISTORY.id,
            PlaylistId.HISTORY.title,
            "",
            R.drawable.common_ic_history_24dp
        )
        val favorite =
            Playlist(
                PlaylistId.FAVORITE.id,
                PlaylistId.FAVORITE.title,
                "",
                R.drawable.common_ic_favorite_24dp
            )
        val later =
            Playlist(
                PlaylistId.LATER.id,
                PlaylistId.LATER.title,
                "",
                R.drawable.common_ic_watch_later_24dp
            )
        val list = playlists?.toMutableList()?.apply {
            add(0, history)
            add(1, favorite)
            add(2, later)
        }
        libraryController.playlists = list
        viewModel.isRefreshing.value = false
    }
}
