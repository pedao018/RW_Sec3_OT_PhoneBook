package com.raywenderlich.rw_sec3_ot_phonebook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raywenderlich.rw_sec3_ot_phonebook.model.Phone

// 1
@Database(entities = arrayOf(Phone::class), version = 1, exportSchema = false)
abstract class PhoneBookDatabase : RoomDatabase() {
    // 2
    abstract fun phoneDao(): PhoneDao

    // 3
    companion object {
        // 4
        private var instance: PhoneBookDatabase? = null

        // 5
        fun getInstance(context: Context): PhoneBookDatabase {
            if (instance == null) {
                // 6
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhoneBookDatabase::class.java,
                    "PhoneBook"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            // 7
            return instance as PhoneBookDatabase
        }
    }
}