package com.example.cat_app.features.details_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.navigation.ARG_SPECIES_ID
import com.example.cat_app.features.allspecies.data.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cat_app.features.details_screen.SpeciesDetailsScreenContract.UiEvent
import com.example.cat_app.features.details_screen.SpeciesDetailsScreenContract.UiState
import com.example.cat_app.features.details_screen.SpeciesDetailsScreenContract.SideEffect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update


@HiltViewModel
class SpeciesDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val breedRepository: BreedRepository,
) : ViewModel() {

    private val breedId = savedStateHandle.get<String>(ARG_SPECIES_ID) ?: "Species not found"    // izvucemo iz prosledjenog argumenta, putem navigacije

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

    // MVI funkcionise tako sto VIEW samo INTENT kad koristik nesto uradi, a VIEWMODEL reaguje na state flow iz modela, BEZ VLASTITOG HARDKODIRANJA inicijalnih dogadjaja
    init {
        observeEvents()

        // ucitacu podatke o rasi macke, za koju sam dobio ID (preko navigacije i savedStateHandle)
        viewModelScope.launch {
            breedRepository.observeBreed(breedId)
                .filterNotNull()
                .collect { breed ->
                    _state.update { it.copy(loading=false, breed=breed) }
                }
        }
    }

    private fun observeEvents() = viewModelScope.launch{
        events.collect { event ->
            // nemam zapravo nikakvu akciju ovde
        }
    }

    /*
    private fun loadTheDetailsForBreed() {
        viewModelScope.launch {
            breedRepository
                .observeBreed(breedId)
                .filterNotNull()
                // ovaj collect je MNOGO BITAN.
                // Ovde se pretplatimo na flow, i collect{...} ce da mi drzi pretplatu STALNO AKTIVNOM
                // svaki put kada se u bazi promeni red sa ovim ID-jem,
                // ROOM ponovo izvrsava upit i emituje novi BREED ENTITY, (pa se desava mapiranje, pa dobijam flow<Breed?>) a moja korutina ga hvata i mapira u UI state
                .collect { breed ->
                    _state.update { ui ->       // composable koji posmatra ovaj state ce se automatski rerenderovati kad se ovaj UiState promeni
                        ui.copy(
                            loading = false,
                            error = null,
                            breed = breed       // GLAVNO: pronalazi breed sa ovim prosledjenim ID, i smesta ga u STANJE.... sada imamo sve neophodno za UI :))
                        )
                    }
                }
                // ova linija bkv nece nikad biti dohvacena, jer collect NE ZAVRSAVA
                // jedino da smo uradili .first(), onda bi se zavrsilo (ali ovo je collect), ili da smo take take(1).collect{...}

                /// BITNO:
                // ROOM-ov DAO FLOW je cold (prekida se kada ga niko ne kolektuje), ali je INFINITE, dakle:
                // collect{..} traje sve dok ga ne cancel-ujem
        }
    }
*/
}