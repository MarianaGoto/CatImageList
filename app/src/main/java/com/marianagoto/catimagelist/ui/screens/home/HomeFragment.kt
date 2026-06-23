package com.marianagoto.catimagelist.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.databinding.FragmentHomeBinding
import com.marianagoto.catimagelist.ui.catlist.CatAdapter
import com.marianagoto.catimagelist.ui.helpers.UIState
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CatAdapter

    private val viewModel: HomeViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CatAdapter { cat ->
            viewModel.toggleFavorite(imageId = cat.id, subId = BuildConfig.SUB_ID)
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
    }

    override fun onResume() {
        super.onResume()

        viewModel.getCats()
    }

    private fun setupObservables(){
        viewModel.cats.observe(viewLifecycleOwner){ uiState ->
            when(uiState){
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}