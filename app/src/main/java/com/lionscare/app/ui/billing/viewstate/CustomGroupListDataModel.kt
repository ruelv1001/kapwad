package com.lionscare.app.ui.billing.viewstate

import androidx.annotation.Keep
import com.lionscare.app.data.repositories.baseresponse.Avatar
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.QrValue
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.group.response.GroupListData

@Keep
data class CustomGroupListDataModel(
    var groupData : GroupListData,
    var isChecked : Boolean = false, //added as additional
)