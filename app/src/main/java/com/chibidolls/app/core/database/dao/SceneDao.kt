package com.chibidolls.app.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chibidolls.app.core.database.entity.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY updatedAt DESC")
    fun getAllScenesFlow(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id LIMIT 1")
    suspend fun getSceneById(id: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Delete
    suspend fun deleteScene(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteSceneById(id: String)
}
