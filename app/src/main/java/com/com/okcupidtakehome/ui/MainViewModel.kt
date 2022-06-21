package com.com.okcupidtakehome.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.com.okcupidtakehome.models.Pet
import com.com.okcupidtakehome.repo.Repo
import com.com.okcupidtakehome.repo.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    private var petsList = listOf<Pet>()
    private val _pets = MutableLiveData<List<Pet>>()
    val pets: LiveData<List<Pet>> = _pets

    private val _topLikedPets = MutableLiveData<List<Pet>>()
    val topLikedPets: LiveData<List<Pet>> = _topLikedPets

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    init {
        getPets()
    }

    fun getPets() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            _error.postValue(false)
            val result = try {
                repo.getPets()
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred:", e)
                Result.Error(e)
            }
            _loading.postValue(false)
            when (result) {
                is Result.Success<List<Pet>> -> {
                    petsList = result.data
                    _pets.postValue(petsList)
                    emitTopLikedPets()
                }
                else -> {
                    _error.postValue(true)
                }
            }
        }
    }

    private fun emitTopLikedPets() {
        _topLikedPets.postValue(
            petsList
                .filter { it.liked }
                .sortedByDescending { it.match }
                .take(6)
        )
    }

    fun petSelected(pet: Pet) {
        petsList = petsList.map {
            if (it.userId == pet.userId) pet.copy(liked = !pet.liked) else it
        }
        _pets.postValue(petsList)
        emitTopLikedPets()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
