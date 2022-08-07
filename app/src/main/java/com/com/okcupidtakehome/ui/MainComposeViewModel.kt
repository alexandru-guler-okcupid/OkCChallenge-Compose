package com.com.okcupidtakehome.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.repo.Repo
import com.com.okcupidtakehome.repo.Result
import com.com.okcupidtakehome.ui.UiState.Loading
import com.com.okcupidtakehome.ui.UiState.ShowError
import com.com.okcupidtakehome.ui.UiState.UpdateList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class MainComposeViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    private val cancelJobsMap = mutableMapOf<String, Job>()

    private var petsList = listOf<PetCard>()

    private val _uiState = MutableLiveData<UiState>(Loading)
    val uiState: LiveData<UiState> get() = _uiState

    init {
        getPets()
    }

    fun getPets() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.postValue(Loading)
            val result = try {
                repo.getPets()
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred:", e)
                Result.Error(e)
            }
            when (result) {
                is Result.Success<List<Pet>> -> {
                    petsList = result.data.map { PetCard(pet = it, isLoading = false) }
                    _uiState.postValue(
                        UpdateList(
                            pets = petsList,
                            topPets = getTopLikedPets()
                        )
                    )
                }
                else -> {
                    _uiState.postValue(ShowError)
                }
            }
        }
    }

    private fun getTopLikedPets(): List<PetCard> =
        petsList
            .filter { it.pet.liked }
            .sortedByDescending { it.pet.match }
            .take(6)

    fun petSelected(pet: Pet) {
        if (pet.liked) {
            _petSelected(pet)
        } else {
            val job = viewModelScope.launch {
                petsList = petsList.map {
                    if (it.pet.userId == pet.userId) {
                        it.copy(isLoading = true)
                    } else {
                        it
                    }
                }
                _uiState.postValue(
                    UpdateList(
                        pets = petsList,
                        topPets = getTopLikedPets()
                    )
                )
                delay(5000L)
                _petSelected(pet)
            }
            cancelJobsMap[pet.userId] = job
        }
    }

    private fun _petSelected(pet: Pet) {
        petsList = petsList.map {
            if (it.pet.userId == pet.userId) {
                it.copy(pet = pet.copy(liked = !pet.liked), isLoading = false)
            } else {
                it
            }
        }
        _uiState.postValue(
            UpdateList(
                pets = petsList,
                topPets = getTopLikedPets()
            )
        )
    }

    fun onPetCancelled(pet: Pet) {
        cancelJobsMap[pet.userId]?.let {
            it.cancel()
            cancelJobsMap.remove(pet.userId)
            petsList = petsList.map { petCard ->
                if (petCard.pet.userId == pet.userId) {
                    petCard.copy(isLoading = false)
                } else {
                    petCard
                }
            }
            _uiState.postValue(
                UpdateList(
                    pets = petsList,
                    topPets = getTopLikedPets()
                )
            )
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}

sealed class UiState {
    object Loading : UiState()

    object ShowError : UiState()

    data class UpdateList(
        val pets: List<PetCard>,
        val topPets: List<PetCard>,
    ) : UiState()
}
