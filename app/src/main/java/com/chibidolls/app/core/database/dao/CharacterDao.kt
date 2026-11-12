package com.chibidolls.app.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chibidolls.app.core.database.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY updatedAt DESC")
    fun getAllCharactersFlow(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: String): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: String)
}
