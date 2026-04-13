package com.adeeba.plantdiseaseapp.data.local

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import com.adeeba.plantdiseaseapp.data.local.dao.DetectionDao
    import com.adeeba.plantdiseaseapp.data.local.entity.DetectionEntity

    @Database(
        entities = [DetectionEntity::class],
        version = 1,
        exportSchema = false
    )
    abstract class AppDatabase : RoomDatabase() {

        abstract fun detectionDao(): DetectionDao

        companion object {

            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "plant_database"
                    ).build()

                    INSTANCE = instance
                    instance
                }
            }
        }
    }