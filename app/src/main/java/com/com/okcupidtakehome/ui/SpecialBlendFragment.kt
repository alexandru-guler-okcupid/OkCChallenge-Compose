package com.com.okcupidtakehome.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.com.okcupidtakehome.databinding.FragmentSpecialBlendBinding
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.util.bindingLifecycle

class SpecialBlendFragment : Fragment(), OnPetSelected {

    private var viewBinding: FragmentSpecialBlendBinding by bindingLifecycle()
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var switchTabs: SwitchTabs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SwitchTabs) {
            switchTabs = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSpecialBlendBinding.inflate(inflater, container, false).also {
        viewBinding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val petsAdapter = PetsAdapter(this)
        viewBinding.specialBlendRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = petsAdapter
        }

        viewBinding.retryButton.setOnClickListener {
            viewModel.getPets()
        }
        viewModel.pets.observe(viewLifecycleOwner) {
            petsAdapter.submitList(it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            viewBinding.progress.isVisible = it && petsAdapter.itemCount == 0
        }
        viewModel.error.observe(viewLifecycleOwner) {
            viewBinding.retryButton.isVisible = it
        }
    }

    override fun onPetSelected(pet: Pet) {
        viewModel.petSelected(pet)
        switchTabs.goToTab(1)
    }

    companion object {
        fun newInstance() = SpecialBlendFragment()
    }
}
