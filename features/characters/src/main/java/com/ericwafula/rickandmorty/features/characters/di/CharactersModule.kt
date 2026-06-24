package com.ericwafula.rickandmorty.features.characters.di

import org.koin.dsl.module

/**
 * Public entry point for the characters feature's graph. The app registers only
 * this module; the feature's internals (its ViewModels) stay hidden.
 */
val charactersModule = module {
    includes(viewModelModule)
}
