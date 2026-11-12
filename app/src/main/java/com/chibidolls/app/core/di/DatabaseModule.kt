package com.chibidolls.app.core.di

import android.content.Context
import androidx.room.Room
import com.chibidolls.app.core.database.AppDatabase
import com.chibidolls.app.core.database.dao.CharacterDao
import com.chibidolls.app.core.database.dao.SceneDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chibi_dolls.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    fun provideSceneDao(database: AppDatabase): SceneDao {
        return database.sceneDao()
    }
}
