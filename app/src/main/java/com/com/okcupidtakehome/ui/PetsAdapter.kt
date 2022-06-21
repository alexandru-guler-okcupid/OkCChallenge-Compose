package com.com.okcupidtakehome.ui

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.com.okcupidtakehome.R
import com.com.okcupidtakehome.databinding.ItemPetBinding
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.util.toBinding

class PetsAdapter(
    private val onPetSelected: OnPetSelected
) : ListAdapter<Pet, PetsAdapter.PetViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder =
        PetViewHolder(parent.toBinding(ItemPetBinding::inflate))

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, onPetSelected)
        }
    }

    class PetViewHolder(
        private val viewBinding: ItemPetBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(pet: Pet, petSelected: OnPetSelected) {
            val context = viewBinding.root.context
            val colorRes = if (pet.liked) R.color.liked else R.color.unliked
            viewBinding.petCardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    colorRes
                )
            )
            viewBinding.petCardView.setOnClickListener {
                petSelected.onPetSelected(pet)
            }
            Glide.with(context.applicationContext)
                .load(pet.photo.original)
                .into(viewBinding.petImage)
            viewBinding.petUserName.text = pet.userName
            viewBinding.petAgeAndLocation.text =
                context.resources
                    .getString(
                        R.string.location,
                        pet.age,
                        pet.location.cityName,
                        pet.location.stateName
                    )

            viewBinding.match.text = context.resources.getString(
                R.string.pet_match,
                pet.matchPerc
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pet>() {
            override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean =
                oldItem == newItem
        }
    }
}
