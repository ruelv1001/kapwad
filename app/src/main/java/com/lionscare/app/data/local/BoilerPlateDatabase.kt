package com.lionscare.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [com.lionscare.app.data.local.UserLocalData::class], version = 1, exportSchema = false)
abstract class BoilerPlateDatabase : RoomDatabase(){
    abstract val userDao : com.lionscare.app.data.local.UserDao
}