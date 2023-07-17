package com.lionscare.app.data.local

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun login(user : com.lionscare.app.data.local.UserLocalData): Long

    @Query("UPDATE users SET access_token = :token WHERE user_id =:userId")
    suspend fun updateToken(userId : Int, token: String)

    @Query("SELECT * FROM users WHERE access_token = :access_token")
    suspend fun getUserInfo(access_token: String): com.lionscare.app.data.local.UserLocalData

    @Query("DELETE FROM users WHERE access_token = :access_token")
    suspend fun logout(access_token: String)
}