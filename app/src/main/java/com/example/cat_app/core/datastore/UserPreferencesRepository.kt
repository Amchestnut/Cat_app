package com.example.cat_app.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private object PreferencesKeys {
    // u PreferencesKeys samo centralizovano definisem sve svoje preference - kljuceve
    val NAME_KEY = stringPreferencesKey("profile_name")     // funkcija iz Jetpack DataStore koja pravi kljuc Preferences.Key<String> pod zadatim imenom "profile_name"
    val NICKNAME_KEY = stringPreferencesKey("profile_nickname")
    val EMAIL_KEY = stringPreferencesKey("profile_email")
}

@Singleton  // Hilt ce kreirati tacno jednu instancu ovog repozitorijuma za ceo zivot aplikacije
class UserPreferencesRepository @Inject constructor(    // DAGGER HILT mi obezbedjuje CONTEXT (Activity ili Application) da ne moram RUCNO da ga prosledjujem kao sto sam debilno radio u mojoj aplikaciji
    @ApplicationContext private val context: Context
){
    // DataStore<Preferences> instanca koju sam dobio iz onog singleton extenzije tamo u utils
    private val dataStore = context.userDataStore

    // dataStore.data je moj objekat koji predstavlja BAZU PREFERENCESA.
    // on ima polje: val data: Flow<Preferences>, asinhroni stream stanja gde svaki put kada se neka vrednost u store-u promeni, u ovom flowu stize novi Preferences objekat
    val nameFlow : Flow<String> = dataStore.data.map {
        it[PreferencesKeys.NAME_KEY] ?: ""      // iz Preferences objekta vadim vrednost za kljuc NAME_KEY, ali on je mozda nullabilan pa stoga ?: ""
    }

    val nicknameFlow: Flow<String> = dataStore.data.map {
        it[PreferencesKeys.NICKNAME_KEY] ?: ""
    }

    val emailFlow : Flow<String> = dataStore.data.map {
        it[PreferencesKeys.EMAIL_KEY] ?: ""
    }

    // If the profile already exists, use it
    val userAlreadyExistsFlow : Flow<Boolean> = combine(nameFlow, nicknameFlow, emailFlow){ name, nick, email ->
        name.isNotBlank() && nick.isNotBlank() && email.isNotBlank()
    }

    // prepare data
    suspend fun saveUserProfile(name: String, nickname: String, email : String){
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NAME_KEY] = name
            prefs[PreferencesKeys.NICKNAME_KEY] = nickname
            prefs[PreferencesKeys.EMAIL_KEY] = email
        }
    }






}