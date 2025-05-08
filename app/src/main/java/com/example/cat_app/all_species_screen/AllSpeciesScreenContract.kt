package com.example.cat_app.all_species_screen

import com.example.cat_app.domain.Breed

interface AllSpeciesScreenContract {

    // 1) Stanje ekrana: sta i kako se renderuje
    data class UiState(
        // ovde trenutno nemam nijedan OBAVEZAN argument, npr passwordId, ali da ga imam, morao bi da da prosledim taj parametar da bi napravio ovaj UI STATE
        val loading: Boolean = false,
        val allBreeds: List<Breed> = emptyList(),
        val searchQuery: String = "",
        val filteredBreeds: List<Breed> = emptyList(),
        val error: Throwable? = null
    )

    // 2) Dogadjaji iz UI‑a (intentovi)
    sealed class UiEvent{
        // Jedan event: na primer korisnik je swipe‑nuo na refresh
        //        data object RefreshData : UiEvent()
        data class SearchQueryChanged(val query: String) : UiEvent()
        object LoadBreeds : UiEvent()
    }

    // 3) Side‑effects (jednokratni “ispod haube” efekti, npr. snack‑bar)
    sealed class SideEffect {
        // No side effects here (for now)
    }
}