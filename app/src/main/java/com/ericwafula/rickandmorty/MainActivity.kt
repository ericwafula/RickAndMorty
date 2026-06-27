package com.ericwafula.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.rememberNavBackStack
import com.ericwafula.rickandmorty.navigation.MainNavDisplay
import com.ericwafula.rickandmorty.navigation.routes.Route
import com.ericwafula.rickandmorty.ui.theme.RickTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickTheme {
                val backStack = rememberNavBackStack(Route.Characters)
                MainNavDisplay(backStack = backStack)
            }
        }
    }
}
