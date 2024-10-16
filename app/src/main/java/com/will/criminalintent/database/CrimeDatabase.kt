package com.will.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.will.criminalintent.data.Crime

@Database(entities = [Crime::class], version = 2, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase: RoomDatabase() {

    abstract fun crimeDao(): CrimeDao
}