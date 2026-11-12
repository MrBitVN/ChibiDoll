package com.chibidolls.app.core.repository

import com.chibidolls.app.core.database.dao.SceneDao
import com.chibidolls.app.core.database.entity.SceneEntity
import com.chibidolls.app.core.model.Scene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SceneRepository @Inject constructor(
    private val sceneDao: SceneDao
) {
    fun getAllScenes(): Flow<List<Scene>> {
        return sceneDao.getAllScenesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getSceneById(id: String): Scene? {
        return sceneDao.getSceneById(id)?.toDomain()
    }

    suspend fun saveScene(scene: Scene) {
        val updated = scene.copy(updatedAt = System.currentTimeMillis())
        sceneDao.insertScene(SceneEntity.fromDomain(updated))
    }

    suspend fun deleteScene(id: String) {
        sceneDao.deleteSceneById(id)
    }
}
