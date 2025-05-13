package com.example.cat_app.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Definisem novi property "userDataStore na svakom Context objektu (Activity, Service, Application)
// (Kao za navigaciju) bilo gde u kodu gde imam context, mogu da napisem context.userDataStore i da dobijem instancu DataStore<Preferences>
val Context.userDataStore by preferencesDataStore(name = "user_prefs")

// DataStore<Preferences> instanca koja cuva sve one moje parove kljuc–vrednost.

/**
val Context.userDataStore by preferencesDataStore(...)
znači: svaki put kad zatražiš context.userDataStore, JVM će pozvati metodu getValue(this, ::userDataStore)
na objektu koji je vratio preferencesDataStore.

Taj objekat–delegat je kreiran jednom (na nivou klase u kojoj si definisao extension)
i “pamti” instancu DataStore-a.
 */