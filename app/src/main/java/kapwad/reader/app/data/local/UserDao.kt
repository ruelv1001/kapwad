package kapwad.reader.app.data.local

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun login(user : UserLocalData): Long

    @Query("UPDATE users SET access_token = :token WHERE id =:userId")
    suspend fun updateToken(userId : String, token: String)

    @Query("SELECT * FROM users WHERE access_token = :access_token")
    suspend fun getUserInfo(access_token: String): UserLocalData

    @Query("DELETE FROM users WHERE access_token = :access_token")
    suspend fun logout(access_token: String)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getOfflineLogin(username: String, password: String): UserLocalData
}