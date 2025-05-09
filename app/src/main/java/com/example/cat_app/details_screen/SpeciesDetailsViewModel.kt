package com.example.cat_app.details_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.ARG_SPECIES_ID
import com.example.cat_app.repository.AllSpeciesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.UiEvent
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.UiState
import com.example.cat_app.details_screen.SpeciesDetailsScreenContract.SideEffect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate


@HiltViewModel
class SpeciesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val allSpeciesRepository: AllSpeciesRepository,
) : ViewModel() {

    private val breedId = savedStateHandle.get<String>(ARG_SPECIES_ID) ?: "Species not found"

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)


    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) = viewModelScope.launch {
        _effect.send(effect)
    }


    init {
        observeEvents()

        setEvent(UiEvent.LoadDetails(breedId))      // Prilikom ucitava DETAILS ekrana, prvi event koji imam je da popunim trenutni STATE da sada imam ovu macku ID ucitanu. Bez nje, nemam sta da ucitavam...
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    // moram da stavim ovo "IS" zbog casting-a, jer ja imam data klasu, a u profinom primeru smo imali "data object"
                    is UiEvent.LoadDetails -> loadDetails(breedId)
                }
            }
        }
    }


    private fun loadDetails(id: String) = viewModelScope.launch {
        setState { copy(loading = true, error = null) }
        try {
            // u repo‑u u praksi su ti svi podaci već kesirani

            // OLD
//            val all = allSpeciesRepository.observeAllSpecies()
//            val b = all.fir { it.id == id }
//                ?: throw IllegalArgumentException("Breed $id not found")

            val breed = allSpeciesRepository
                .observeBreed(id)
                .first()                 // suspend: uzmi prvu emisiju
                ?: throw IllegalArgumentException("Breed $id not found")

            setState {
                copy(loading = false, breed = breed)
            }
        } catch (t: Throwable) {
            setState {
                copy(loading = false, error = t)
            }
//            _sideEffects.emit(
//                SpeciesDetailsScreenContract.SideEffect.ShowErrorMessage(
//                    t.localizedMessage ?: "Unknown error"
//                )
//            )
        }
    }

}