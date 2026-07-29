package com.marianagoto.catimagelist.ui.screens.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.databinding.FragmentFavoritesBinding
import com.marianagoto.catimagelist.ui.adapter.FavoriteCatAdapter
import com.marianagoto.catimagelist.ui.state.FavoriteEvent
import com.marianagoto.catimagelist.ui.state.UIState
import com.marianagoto.catimagelist.ui.util.ToastHelper.ShowCustomSnackbar
import com.marianagoto.catimagelist.ui.util.lifecycleScopeRepeat
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteCatAdapter

    private val viewModel: FavoritesViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoriteCatAdapter { cat ->
            Log.d("CatAdapter", "favoriteid: ${cat.favoriteId}")

            viewModel.removeFavoriteCat(
                favoriteId = cat.favoriteId,
                breedName = cat.breedName
            )
        }

        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewFavorites.adapter = adapter

        setupObservables()

        binding.errorLayout.btnRetry.setOnClickListener {
            viewModel.getFavoriteCats()
        }
    }

    private fun setupObservables() {
        lifecycleScopeRepeat {
            viewModel.cats.collect { uiState ->
                when (uiState) {
                    is UIState.Loading -> {
                        binding.ltLoading.visibility = View.VISIBLE
                        binding.errorLayout.nsError.visibility = View.GONE
                        binding.recyclerViewFavorites.visibility = View.GONE
                    }

                    is UIState.Error -> {
                        binding.ltLoading.visibility = View.GONE
                        binding.errorLayout.nsError.visibility = View.VISIBLE
                        binding.recyclerViewFavorites.visibility = View.GONE
                    }

                    is UIState.Success -> {
                        binding.ltLoading.visibility = View.GONE
                        binding.errorLayout.nsError.visibility = View.GONE
                        binding.recyclerViewFavorites.visibility = View.VISIBLE

                        val catImageList = uiState.data
                        adapter.submitList(catImageList)
                    }
                }
            }
        }
        lifecycleScopeRepeat {
            viewModel.favoritesEvent.collect { event ->
                when (event) {
                    is FavoriteEvent.Success -> {
                        ShowCustomSnackbar(view = binding.root, breedName = event.message)
                    }
                    is FavoriteEvent.Error -> {
                        ShowCustomSnackbar(view = binding.root, breedName = event.message)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteCats()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}