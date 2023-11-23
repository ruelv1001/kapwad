package com.ziacare.app.ui.billing.viewstate

import androidx.annotation.Keep
import com.ziacare.app.data.repositories.group.response.GroupListData

@Keep
data class CustomGroupListDataModel(
    var groupData : GroupListData,
    var isChecked : Boolean = false, //added as additional
)