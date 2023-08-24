package com.lionscare.app.data.repositories.auth

import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val userDao: com.lionscare.app.data.local.UserDao
){
    suspend fun login(user : com.lionscare.app.data.local.UserLocalData) = userDao.login(user)
    suspend fun updateToken(userId: String,token: String) = userDao.updateToken(userId, token)
    suspend fun logout(accessToken: String) = userDao.logout(accessToken)
    suspend fun getUserInfo(access_token : String) = userDao.getUserInfo(access_token)
}