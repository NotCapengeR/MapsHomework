package ru.netology.mapshomework

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.netology.mapshomework.core.di.AppComponent
import ru.netology.mapshomework.core.di.DaggerAppComponent
import ru.netology.mapshomework.core.di.modules.AppModule
import ru.netology.mapshomework.utils.getAppComponent
import timber.log.Timber

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }


    override fun onCreate() {
        super.onCreate()
        getAppComponent().inject(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        MapKitFactory.setApiKey(getString(R.string.map_yandex_api_key))
    }
}