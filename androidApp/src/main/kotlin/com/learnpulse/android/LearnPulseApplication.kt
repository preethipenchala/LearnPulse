package com.learnpulse.android

import android.app.Application
import com.learnpulse.di.androidModule
import com.learnpulse.di.commonModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class LearnPulseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@LearnPulseApplication)
            modules(commonModules + androidModule)
        }
    }
}
