package ru.netology.mapshomework.core.di

import dagger.Component
import ru.netology.mapshomework.App
import ru.netology.mapshomework.core.di.modules.AppModule
import ru.netology.mapshomework.core.di.modules.MemoryModule
import ru.netology.mapshomework.core.di.modules.ViewModelModule
import ru.netology.mapshomework.ui.MainActivity
import ru.netology.mapshomework.ui.MainFragment
import ru.netology.mapshomework.ui.MapsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [MemoryModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent
    }

    fun inject(application: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: MapsFragment)
}