package com.com.okcupidtakehome.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.repo.Repo
import com.com.okcupidtakehome.repo.Result
import com.com.okcupidtakehome.ui.MatchUiState.UpdateTopPetsList
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

    private val _matchUiState = MutableLiveData<MatchUiState>()
    val matchUiState: LiveData<MatchUiState> get() = _matchUiState

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
                    _uiState.postValue(UpdateList(petsList))
                    emitTopLikedPets()
                }
                else -> {
                    _uiState.postValue(ShowError)
                }
            }
        }
    }

    private fun emitTopLikedPets() {
        // Can we use the same ui state object here or do we need a seperate one?
        _matchUiState.postValue(
            UpdateTopPetsList(
                petsList
                    .filter { it.pet.liked }
                    .sortedByDescending { it.pet.match }
                    .take(6)
            )
        )
    }

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
                _uiState.postValue(UpdateList(petsList))
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
        _uiState.postValue(UpdateList(petsList))
        emitTopLikedPets()
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
            _uiState.postValue(UpdateList(petsList))
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
        // todo maybe put top liked pets here?
    ) : UiState()
}

sealed class MatchUiState {
    data class UpdateTopPetsList(
        val pets: List<PetCard>
    ) : MatchUiState()
}
