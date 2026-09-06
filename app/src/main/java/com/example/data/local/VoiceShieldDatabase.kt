package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScamCallEntity::class], version = 1, exportSchema = false)
abstract class VoiceShieldDatabase : RoomDatabase() {
    abstract fun scamCallDao(): ScamCallDao

    companion object {
        @Volatile
        private var INSTANCE: VoiceShieldDatabase? = null

        fun getDatabase(context: Context): VoiceShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoiceShieldDatabase::class.java,
                    "voice_shield_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
