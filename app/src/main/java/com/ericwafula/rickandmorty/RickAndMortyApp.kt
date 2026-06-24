package com.ericwafula.rickandmorty

import android.app.Application
import com.ericwafula.rickandmorty.helpers.initKoin

class RickAndMortyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
