package com.lionscare.app.data.repositories.auth

import com.lionscare.app.data.local.UserLocalData
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val userDao: com.lionscare.app.data.local.UserDao
){
    suspend fun login(user : UserLocalData) = userDao.login(user)
    suspend fun updateToken(userId: String,token: String) = userDao.updateToken(userId, token)
    suspend fun logout(accessToken: String) = userDao.logout(accessToken)
    suspend fun getUserInfo(access_token : String) = userDao.getUserInfo(access_token)
}