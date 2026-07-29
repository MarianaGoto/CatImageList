package com.marianagoto.catimagelist.ui.screens.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.databinding.FragmentHomeBinding
import com.marianagoto.catimagelist.ui.adapter.HomeCatAdapter
import com.marianagoto.catimagelist.ui.state.FavoriteEvent
import com.marianagoto.catimagelist.ui.state.FavoriteUpdateEvent
import com.marianagoto.catimagelist.ui.util.ToastHelper.ShowCustomSnackbar
import com.marianagoto.catimagelist.ui.state.UIState
import com.marianagoto.catimagelist.ui.util.lifecycleScopeRepeat
import com.marianagoto.catimagelist.ui.vo.CatItemVO
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeCatAdapter

    private val viewModel: HomeViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = HomeCatAdapter { catItem ->
            viewModel.toggleFavorite(catItem)
        }

        binding.recyclerViewHome.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewHome.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getCats()
            if (binding.swipeRefresh.isRefreshing) {
                binding.ltLoading.visibility = View.VISIBLE
                binding.recyclerViewHome.visibility = View.GONE
            }
            binding.swipeRefresh.isRefreshing = false
        }

        setupObservables()

        binding.errorLayout.btnRetry.setOnClickListener{
            viewModel.getCats()
        }

    }

    override fun onResume() {
        super.onResume()

        viewModel.getCats()
    }

    private fun setupObservables() {
        lifecycleScopeRepeat {
            viewModel.cats.collect { uiState ->
                when (uiState) {
                    is UIState.Loading -> {
                        binding.ltLoading.visibility = View.VISIBLE
                        binding.errorLayout.nsError.visibility = View.GONE
                        binding.recyclerViewHome.visibility = View.GONE
                    }

                    is UIState.Error -> {
                        binding.ltLoading.visibility = View.GONE
                        binding.errorLayout.nsError.visibility = View.VISIBLE
                        binding.recyclerViewHome.visibility = View.GONE
                    }

                    is UIState.Success -> {
                        binding.ltLoading.visibility = View.GONE
                        binding.errorLayout.nsError.visibility = View.GONE
                        binding.recyclerViewHome.visibility = View.VISIBLE
                        val catItems = uiState.data.map { cat ->
                            CatItemVO(image = cat.image, favoriteId = null)
                        } //fazer mapper
                        adapter.submitList(catItems)
                    }
                }
            }
        }

        lifecycleScopeRepeat {
            viewModel.favoriteEvent.collect { event ->
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

        lifecycleScopeRepeat {
            viewModel.favoriteUpdateEvent.collect { event ->
                when (event) {
                    is FavoriteUpdateEvent.FavoriteAdded -> {
                        // A lista já foi atualizada via updateCatFavoriteStatus()
                        // Adapter se atualiza automaticamente via StateFlow
                    }
                    is FavoriteUpdateEvent.FavoriteRemoved -> {
                        // A lista já foi atualizada via updateCatFavoriteStatus()
                        // Adapter se atualiza automaticamente via StateFlow
                    }
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}