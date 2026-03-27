package com.learnpulse.di

import com.learnpulse.data.local.DatabaseDriverFactory
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { Android.create() }
}
