package com.example.cat_app.all_species_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.cat_app.all_species_screen.AllSpeciesScreenContract.UiState
import com.example.cat_app.all_species_screen.AllSpeciesScreenContract.UiEvent
import com.example.cat_app.all_species_screen.AllSpeciesScreenContract.SideEffect
import com.example.cat_app.domain.Breed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Ovom anotacijom kazem Hilt‑u da je moja klasa ViewModel i da je treba registrovati u ViewModelComponent (scope koji traje koliko i sam ViewModel).
// Hilt ce zbog toga automatski generisati potrebnu fabriku (factory) i “ugraditi” je u DI graf.
@HiltViewModel
class AllSpeciesViewModel @Inject constructor(
    private val allSpeciesRepository : AllSpeciesRepository,    // zapravo ja mu injectujem AllSpeciesRepositoryInMemory, tj konkretnu implementaciju, jer je AllSpeciesRepository samo interfejs.
) : ViewModel(){
    // MutableStateFlow je „hot“ (uvek aktivan) tok podataka koji cuva tacno 1 vrednost.

    private val _state = MutableStateFlow(UiState())        // UiState() poziva primarni konstruktor tvoje data class UiState(...) i postavlja podrazumevani pocetni state (npr. loading = true, data = emptyList(), …).
    val state = _state.asStateFlow()                        //  READ-ONLY  (StateFlow<UiState>), da spolja niko ne moze direktno da menja _state.
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)     // azuriranje stanja


    // MutableSharedFlow je takođe „hot“ tok, ali ne čuva poslednju vrednost (po defaultu replay = 0). Koristi se za jednokratne dogadjaje (npr. klik, swipe-to-refresh).
    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            events.emit(event)  // emit(event)—u korutini ubacuješ jedan UiEvent u tok. Svi koji su collect‑ovali taj SharedFlow get‑uju taj event jednom.
        }
    }


    // side effects, mada ih mi ovde u all species generalno nemamo?
    private val _effect: Channel<SideEffect> = Channel()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(effect: SideEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }


    init {
        Log.d("AllSpeciesVM", "init: line before observeEvents()")
        observeEvents()
        Log.d("AllSpeciesVM", "init: line before setEvent()")
        setEvent(UiEvent.LoadBreeds)
    }

    private fun observeEvents() = viewModelScope.launch {
        Log.d(TAG, "observeEvents: collecting events")
        events.collect { event ->
            Log.d(TAG, "observeEvents: got event = $event")
            when (event) {
                UiEvent.LoadBreeds -> loadBreeds()
                is UiEvent.SearchQueryChanged -> applySearch(event.query)
            }
        }
    }

    // pre smo imali hardkodovano, sad API
    private fun loadBreeds() = viewModelScope.launch {
        Log.d(TAG, "loadBreeds() start")
        setState { copy(loading = true, error = null) }
        try {
            val list = allSpeciesRepository.getAllSpecies()
            Log.d(TAG, "loadBreeds: repo returned ${list.size} items")

            // Ako hocu da tokom LOAD-a primenim postojeci query (tj user je vec nesto ukucao pre nego sto je lista stigla), uradicu ovo:
            val currentQuery = _state.value.searchQuery
            val initialFiltered = if (currentQuery.isBlank()) {
                list
            } else {
                list.filter { it.name.contains(currentQuery, ignoreCase = true) }
            }

            setState {
                copy(
                    loading = false,
                    allBreeds = list,
                    filteredBreeds = initialFiltered
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "loadBreeds: error fetching breeds", t)
            setState { copy(loading = false, error = t) }
        }
    }

    private fun applySearch(query: String) = viewModelScope.launch {
        setState {
            // ovde izračunam novi filtered list na osnovu query‑ja
            val filtered = allBreeds.filter { it.name.contains(query, ignoreCase = true) }
            copy(
                searchQuery = query,
                filteredBreeds = filtered
            )
        }
    }




}