package com.marianagoto.catimagelist.ui.screens.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.databinding.FragmentFavoritesBinding
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.ui.catlist.CatAdapter
import com.marianagoto.catimagelist.ui.helpers.UIState
import com.marianagoto.catimagelist.ui.screens.home.HomeViewModel
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CatAdapter

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

        adapter = CatAdapter {
            viewModel.getFavoriteCats()
//            viewModel.toggleFavorite(imageId = cat.id, subId = BuildConfig.SUB_ID)
//            chamar a função de delete do gato de favoritos
        }

        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewFavorites.adapter = adapter

        setupObservables()

        binding.errorLayout.btnRetry.setOnClickListener{
            viewModel.getFavoriteCats()
        }
    }

    private fun setupObservables(){
        viewModel.cats.observe(viewLifecycleOwner){ uiState ->
            when(uiState){
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

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteCats()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}