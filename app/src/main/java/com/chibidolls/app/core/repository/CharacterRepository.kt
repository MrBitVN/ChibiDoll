package com.chibidolls.app.core.repository

import com.chibidolls.app.core.database.dao.CharacterDao
import com.chibidolls.app.core.database.entity.CharacterEntity
import com.chibidolls.app.core.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val characterDao: CharacterDao
) {
    fun getAllCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharactersFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCharacterById(id: String): Character? {
        return characterDao.getCharacterById(id)?.toDomain()
    }

    suspend fun saveCharacter(character: Character) {
        val updated = character.copy(updatedAt = System.currentTimeMillis())
        characterDao.insertCharacter(CharacterEntity.fromDomain(updated))
    }

    suspend fun deleteCharacter(id: String) {
        characterDao.deleteCharacterById(id)
    }
}
