package pe.edu.upc.tripmatch.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.edu.upc.tripmatch.data.local.entity.FavoriteExperienceEntity


@Dao
interface ExperienceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteExperienceEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteExperienceEntity)

    @Query("SELECT * FROM favorite")
    suspend fun getAllFavorites(): List<FavoriteExperienceEntity>

    @Query("SELECT * FROM favorite WHERE id = :experienceId LIMIT 1")
    suspend fun getFavoriteById(experienceId: Int): FavoriteExperienceEntity?


}