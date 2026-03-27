package com.learnpulse

import com.learnpulse.di.commonModules
import com.learnpulse.di.iosModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(commonModules + iosModule)
    }
}
