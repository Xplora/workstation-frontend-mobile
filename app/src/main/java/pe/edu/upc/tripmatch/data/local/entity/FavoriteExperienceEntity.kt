package pe.edu.upc.tripmatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteExperienceEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val duration: Int,
    val price: Double,
    val categoryName: String,
    val agencyName: String
)