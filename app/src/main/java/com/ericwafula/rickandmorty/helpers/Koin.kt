package com.ericwafula.rickandmorty.helpers

import android.app.Application
import com.ericwafula.rickandmorty.data.domain.di.domainModule
import com.ericwafula.rickandmorty.features.characters.di.charactersModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Bootstraps Koin for the application. Registers the domain graph plus each
 * feature's public module; the app never references a data source or a feature's
 * internals directly.
 */
fun Application.initKoin() {
    startKoin {
        androidLogger()
        androidContext(this@initKoin)
        modules(
            domainModule,
            charactersModule,
        )
    }
}
