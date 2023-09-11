package com.lionscare.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [UserLocalData::class], version = 3, exportSchema = false)
abstract class BoilerPlateDatabase : RoomDatabase(){
    abstract val userDao : UserDao
}