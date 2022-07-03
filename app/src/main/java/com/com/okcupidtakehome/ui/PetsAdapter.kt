package com.com.okcupidtakehome.ui

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.com.okcupidtakehome.R
import com.com.okcupidtakehome.databinding.ItemPetBinding
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.util.toBinding

class PetsAdapter(
    private val onPetSelected: OnPetSelected,
    private val onPetCancelled: OnPetCancelled?
) : ListAdapter<PetCard, PetsAdapter.PetViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder =
        PetViewHolder(parent.toBinding(ItemPetBinding::inflate))

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, onPetSelected, onPetCancelled)
        }
    }

    class PetViewHolder(
        private val viewBinding: ItemPetBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(
            petCard: PetCard,
            petSelected: OnPetSelected,
            petCancelled: OnPetCancelled?
        ) {
            val context = viewBinding.root.context
            val pet = petCard.pet
            val colorRes = if (pet.liked) R.color.liked else R.color.unliked
            viewBinding.petCardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    colorRes
                )
            )
            viewBinding.petCardView.setOnClickListener {
                if (!petCard.isLoading) {
                    petSelected.onPetSelected(pet)
                }
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
            viewBinding.cancelButton.isVisible = petCard.isLoading
            viewBinding.cancelButton.setOnClickListener {
                petCancelled?.onPetCancelled(pet)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PetCard>() {
            override fun areItemsTheSame(oldItem: PetCard, newItem: PetCard): Boolean =
                oldItem.pet.userId == newItem.pet.userId

            override fun areContentsTheSame(oldItem: PetCard, newItem: PetCard): Boolean =
                oldItem == newItem
        }
    }
}

data class PetCard(
    val pet: Pet,
    val isLoading: Boolean
)
