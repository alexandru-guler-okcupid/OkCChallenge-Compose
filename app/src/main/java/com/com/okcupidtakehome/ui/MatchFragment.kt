package com.com.okcupidtakehome.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.com.okcupidtakehome.databinding.FragmentMatchBinding
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.util.bindingLifecycle

class MatchFragment : Fragment(), OnPetSelected {

    private var viewBinding: FragmentMatchBinding by bindingLifecycle()
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMatchBinding.inflate(inflater, container, false).also {
        viewBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val petsAdapter = PetsAdapter(this)
        viewBinding.matchRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = petsAdapter
        }
        viewModel.topLikedPets.observe(viewLifecycleOwner) {
            petsAdapter.submitList(it)
        }
    }

    override fun onPetSelected(pet: Pet) {
        viewModel.petSelected(pet)
    }

    companion object {
        fun newInstance() = MatchFragment()
    }
}
