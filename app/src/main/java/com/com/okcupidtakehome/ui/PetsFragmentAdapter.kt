package com.com.okcupidtakehome.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PetsFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SpecialBlendFragment.newInstance()
            1 -> MatchFragment.newInstance()
            else -> throw IllegalStateException("Unknown position for pager: $position")
        }
    }
}
