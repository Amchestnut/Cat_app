package com.example.cat_app.details_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.ARG_SPECIES_ID
import com.example.cat_app.all_species_screen.AllSpeciesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.UiEvent
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.UiState
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.SideEffect


@HiltViewModel
class SpeciesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val allSpeciesRepository: AllSpeciesRepository,
) : ViewModel() {

//    private val breedId = savedStateHandle.get<>()

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SideEffect>()
    val sideEffects = _sideEffects.asSharedFlow()

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch { events.emit(event) }

    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) = viewModelScope.launch { _effect.send(effect) }


//    init {
//        // automatski pucaj LoadDetails event iz SavedStateHandle
//        savedStateHandle.get<String>("speciesId")?.let { id ->
//            onEvent(UiEvent.LoadDetails(id))
//        }
//    }

    init {
        // iz navigacije stiže String id iz argumenata
        savedStateHandle.get<String>(ARG_SPECIES_ID)
            ?.let { loadDetails(it) }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadDetails -> loadDetails(event.speciesId)
        }
    }

    private fun loadDetails(id: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        try {
            // u repo‑u u praksi su ti svi podaci već keširani
            val all = allSpeciesRepository.getAllSpecies()
            val b = all.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Breed $id not found")
            _state.update {
                it.copy(loading = false, breed = b)
            }
        } catch (t: Throwable) {
            _state.update {
                it.copy(loading = false, error = t)
            }
//            _sideEffects.emit(
//                SpeciesDetailsScreenContract.SideEffect.ShowErrorMessage(
//                    t.localizedMessage ?: "Unknown error"
//                )
//            )
        }
    }

}