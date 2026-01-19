package com.marianagoto.catimagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marianagoto.catimagelist.databinding.FragmentEmptyFavoritesBinding


class EmptyFavoritesFragment : Fragment() {
    private var _binding: FragmentEmptyFavoritesBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmptyFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ltEmptyFavorites
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Opcional: pausar animação ao destruir view para economizar recursos
        binding.ltEmptyFavorites.pauseAnimation()
        _binding = null
    }
}