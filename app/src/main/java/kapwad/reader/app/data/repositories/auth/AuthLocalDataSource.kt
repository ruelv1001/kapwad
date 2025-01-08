package kapwad.reader.app.data.repositories.auth

import kapwad.reader.app.data.local.UserLocalData
import kapwad.reader.app.data.repositories.meter.MeterLocalDataSource
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val userDao: kapwad.reader.app.data.local.UserDao
){
    //suspend fun login(user : MeterLocalDataSource) = userDao.login(user)
    suspend fun updateToken(userId: String,token: String) = userDao.updateToken(userId, token)
    suspend fun logout(accessToken: String) = userDao.logout(accessToken)
    suspend fun getUserInfo(access_token : String) = userDao.getUserInfo(access_token)
}