package com.marianagoto.catimagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.marianagoto.catimagelist.databinding.FragmentHomeBinding
import com.marianagoto.catimagelist.ui.catlist.CatAdapter
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CatAdapter

    private val viewModel: CatViewModel by activityViewModels()


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
            viewModel.toggleFavorite(cat)
        }

        binding.recyclerViewHome.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewHome.adapter = adapter

        viewModel.cats.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}