package com.example.cat_app.all_species_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.example.cat_app.all_species_screen.AllSpeciesScreenContract.UiState
import com.example.cat_app.all_species_screen.AllSpeciesScreenContract.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.getAndUpdate
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

    init {
        setEvent(UiEvent.LoadBreeds)
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is UiEvent.LoadBreeds -> loadBreeds()
                    is UiEvent.SearchQueryChanged -> applySearch(event.query)
                }
            }
        }
    }

    private fun loadBreeds() = viewModelScope.launch {
        // Ovo je mesto gde bi zvao API; za sada hardkod:
        val hardcoded = listOf(
            Breed(1, "Abyssinian"),
            Breed(2, "Bengal"),
            Breed(3, "Maine Coon"),
            Breed(4, "Persian"),
            Breed(5, "Siamese"),
            Breed(6, "Sphynx")
        )
        setState {
            copy(
                loading = false,
                allBreeds = hardcoded,
                filteredBreeds = hardcoded
            )
        }
    }

    private fun applySearch(query: String) = viewModelScope.launch {
        setState {
            copy(
                searchQuery = query,
                filteredBreeds = allBreeds.filter { it.name.contains(query, ignoreCase = true) }
            )
        }
    }




}