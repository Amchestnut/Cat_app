package com.example.cat_app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationHilt : Application(){

    override fun onCreate() {
        super.onCreate()
        Log.d("Test", "App:onCreate()")
    }


}


/*
@HiltAndroidApp na mojoj Application klasi generiše koren DI‑grafa (SingletonComponent) i pokreće Hilt‑ovu integraciju s Android lifecycle‑om.

Kada Activity-ju dodam @AndroidEntryPoint, Hilt očekuje da postoji ta Application klasa u AndroidManifestu, kako bi mogao da kreira i vezuje ActivityComponent.

Ako nemam u manifestu <application android:name="…"> deklarisan taj @HiltAndroidApp Application, Hilt ne može da nađe svoj root i baci IllegalStateException.
 */