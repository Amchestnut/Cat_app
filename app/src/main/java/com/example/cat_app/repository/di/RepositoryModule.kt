package com.example.cat_app.repository.di

import com.example.cat_app.repository.AllSpeciesRepository
import com.example.cat_app.repository.BreedRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Binds


// AKO KORISTIMO APSTRAKTNU KLASU, ONDA KORISTIMO @BINDS UMESTO @PROVIDES.
// zapravo bolji pristup (brzi za nijansu)

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryModule {
//
//    @Binds
//    @Singleton
//    abstract fun bindAllSpeciesRepository(
//        impl: BreedRepository
//    ): AllSpeciesRepository
//}


// @PROVIDES SU BUKVALNO STATICKE METODE, i dalje RADIMO INJECT, ali sada HILT generise kod koji, u momentu potrebe za tom zavisnoscu, poziva tu metodu i koristi rezultat
// ali sada ja definisem metodu u modulu, koja VRACA TRAZENI TIP.   Npr, kod @BINDS, hilt radi KASTOVANJE.

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideRepo(impl: BreedRepository): AllSpeciesRepository = impl
}
