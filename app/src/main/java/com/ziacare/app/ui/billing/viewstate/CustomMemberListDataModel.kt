package com.ziacare.app.ui.billing.viewstate

import androidx.annotation.Keep
import com.ziacare.app.data.repositories.member.response.MemberListData

@Keep
data class CustomMemberListDataModel(
    var memberListData : MemberListData,
    var isChecked : Boolean = false, //added as additional
)