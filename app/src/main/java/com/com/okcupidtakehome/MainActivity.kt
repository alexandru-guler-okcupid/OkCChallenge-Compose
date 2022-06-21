package com.com.okcupidtakehome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.com.okcupidtakehome.databinding.ActivityMainBinding
import com.com.okcupidtakehome.ui.PetsFragmentAdapter
import com.com.okcupidtakehome.ui.SwitchTabs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SwitchTabs {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupTabLayout()
    }

    private fun setupTabLayout() {
        val petsFragmentAdapter = PetsFragmentAdapter(this)
        viewBinding.pager.adapter = petsFragmentAdapter
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.special_blend)
                1 -> getString(R.string.match_perc)
                else -> throw IllegalStateException("Unknown position for TabLayoutMediator: $position")
            }
        }.attach()
    }

    override fun goToTab(position: Int) {
        if (position != viewBinding.tabLayout.selectedTabPosition) {
            viewBinding.tabLayout.setScrollPosition(position, 0f, false)
            viewBinding.pager.currentItem = position
        }
    }
}
