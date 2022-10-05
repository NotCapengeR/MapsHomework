package ru.netology.mapshomework.core.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.netology.mapshomework.App
import ru.netology.mapshomework.core.db.MapDB
import ru.netology.mapshomework.core.db.MapDB.Companion.DB_NAME
import ru.netology.mapshomework.core.db.marker.MarkerDAO
import ru.netology.mapshomework.core.repository.MarksRepository
import ru.netology.mapshomework.core.repository.MarksRepositoryImpl
import javax.inject.Singleton

@Module(includes = [AppModule::class, RepositoryBinder::class])
class MemoryModule {

    companion object {
        const val PREFS_KEY: String = "maps_pref"
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideDB(context: Context): MapDB = Room.databaseBuilder(
        context,
        MapDB::class.java,
        DB_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideMarkersDAO(db: MapDB): MarkerDAO = db.getMarkerDao()
}

@Module
class AppModule(private val application: App) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun provideApp(): Application = application

    @Provides
    fun provideMapKit(): MapKit = MapKitFactory.getInstance()
}

@Module
interface RepositoryBinder {


    @Binds
    fun bindMarksRepository(repositoryImpl: MarksRepositoryImpl): MarksRepository
}