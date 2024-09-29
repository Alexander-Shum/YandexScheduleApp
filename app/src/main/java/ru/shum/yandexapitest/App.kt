package ru.shum.yandexapitest

import android.app.Application
import org.koin.core.context.startKoin
import ru.shum.appModule
import ru.shum.data.dataModule
import ru.shum.domain.domainModule
import org.koin.android.ext.koin.androidContext

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule, dataModule, domainModule)

            androidContext(this@App)
        }
    }
}