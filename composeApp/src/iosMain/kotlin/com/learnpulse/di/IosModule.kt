package com.learnpulse.di

import com.learnpulse.data.local.DatabaseDriverFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { Darwin.create() }
}
