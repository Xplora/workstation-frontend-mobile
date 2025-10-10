package pe.edu.upc.tripmatch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.edu.upc.tripmatch.data.local.dao.ExperienceDao
import pe.edu.upc.tripmatch.data.local.entity.FavoriteExperienceEntity

@Database(entities = [FavoriteExperienceEntity::class], version = 2, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun experienceDao(): ExperienceDao
}