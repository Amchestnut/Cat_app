package com.example.cat_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cat_app.core.ui.BottomNavigation
//import com.example.cat_app.core.ui.FinalNavigation
//import com.example.cat_app.core.ui.MainNavigation
//import com.example.cat_app.core.ui.bottombarnavigation.AppNavigation
import com.example.cat_app.core.ui.theme.Cat_appTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint      // ovo primorava HILT da kreira ActivityComponent i injectuje sve @Inject polja ili zaivnosti u Activity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cat_appTheme {
//                AppNavigation()
//                MainNavigation()

                BottomNavigation()

//                FinalNavigation()
            }
        }
    }

}