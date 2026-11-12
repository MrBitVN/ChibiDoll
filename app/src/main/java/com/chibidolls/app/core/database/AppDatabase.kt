package com.chibidolls.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chibidolls.app.core.database.converter.Converters
import com.chibidolls.app.core.database.dao.CharacterDao
import com.chibidolls.app.core.database.dao.SceneDao
import com.chibidolls.app.core.database.entity.CharacterEntity
import com.chibidolls.app.core.database.entity.SceneEntity

@Database(
    entities = [CharacterEntity::class, SceneEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun sceneDao(): SceneDao
}
