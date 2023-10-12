package com.lionscare.app.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.lionscare.app.security.AuthEncryptedDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor (
    private val authEncryptedDataManager: AuthEncryptedDataManager
) : ViewModel()  {
    val user = authEncryptedDataManager.getUserBasicInfo()
}