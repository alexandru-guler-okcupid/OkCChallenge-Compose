package com.com.okcupidtakehome.ui

import com.com.okcupidtakehome.models.Pet

interface OnPetSelected {
    fun onPetSelected(pet: Pet)
}

interface SwitchTabs {
    fun goToTab(position: Int)
}
