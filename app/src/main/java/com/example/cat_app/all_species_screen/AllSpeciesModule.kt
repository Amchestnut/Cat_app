package com.example.cat_app.all_species_screen

import com.example.cat_app.repository.AllSpeciesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module → klasa ili objekt s metodama koje pružaju (provide) zavisnosti.

// vise ne treba ovo, trebalo za prvi domaci
//@Module                                     // sa @Module govorimo HILT-u: "u ovom objektu definises metode koje daju (provide) zavisnosti"
//@InstallIn(SingletonComponent::class)       // definiše u koji scope/komponentu modul ide (SingletonComponent za ceo app, ViewModelComponent za VM, itd.).
//object AllSpeciesModule {
//
////    @Provides                               // konkretna metoda koja vraca instancu nekog tipa (AllSpeciesRepository)
//    @Provides
//    @Singleton                              // jednom stvori ovaj objekat, i deli ga svuda (u okviru singleton component)
//    fun provideAllSpeciesRepository(impl: AllSpeciesRepositoryRetrofit): AllSpeciesRepository = impl
//}



/*
@InstallIn(SingletonComponent::class)       // ovime direktno "INSTALIRAMO" modul i ucitavamo ga u SINGLETON COMPONENT, tj u root naseg HILT GRAFA !!!!!!
Sve u ovom modulu ce da ZIVI JEDNAKO DUGO kao i sama aplikacija
 */