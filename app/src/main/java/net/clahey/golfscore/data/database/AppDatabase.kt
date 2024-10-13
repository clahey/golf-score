package net.clahey.golfscore.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Player::class, Game::class, GameToPlayer::class, Score::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao

    companion object {
        private const val Database_NAME = "golf_scores.db"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, AppDatabase::class.java, Database_NAME
                    )  .fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}