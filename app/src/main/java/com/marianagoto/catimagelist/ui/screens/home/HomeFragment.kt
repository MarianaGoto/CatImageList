package com.marianagoto.catimagelist.ui.screens.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.databinding.FragmentHomeBinding
import com.marianagoto.catimagelist.ui.catlist.HomeCatAdapter
import com.marianagoto.catimagelist.ui.helpers.ToastHelper.ShowCustomSnackbar
import com.marianagoto.catimagelist.ui.helpers.UIState
import com.marianagoto.catimagelist.ui.util.lifecycleScopeRepeat
import kotlinx.coroutines.launch
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


        adapter = HomeCatAdapter { cat ->
            if(cat.isFavorite){
                viewModel.toggleFavorite(cat = cat, subId = BuildConfig.SUB_ID)
            } else {
//                cat.favoriteId?.let{ id ->
//                    viewModel.removeFavoriteCat(id)
//                    cat.favoriteId = null
//                }?: run {
//                    // Caso de segurança: se não temos o ID ainda (ex: API lenta)
//                    Log.e("HomeFragment", "Não é possível remover: ID do favorito ainda não recebido")
//                }
            }
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
                        adapter.submitList(uiState.data)
                    }
                }
            }
        }

        lifecycleScopeRepeat {
            viewModel.favoriteBreedName.collect { favoriteBreedName ->
                ShowCustomSnackbar(view = binding.root, breedName = favoriteBreedName)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}