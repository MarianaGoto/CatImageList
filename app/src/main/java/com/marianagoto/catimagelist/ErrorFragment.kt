package com.marianagoto.catimagelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.marianagoto.catimagelist.databinding.FragmentErrorBinding
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel

class ErrorFragment : Fragment() {
    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botão de retry
        binding.btnRetry.setOnClickListener {
            viewModel.loadCats()
        }
    }
}