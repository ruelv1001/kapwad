package com.lionscare.app.data.model

import androidx.annotation.Keep

@Keep
data class AccountTypeModel(
    var title: String? = null,
    var icon: Int ? = 0,
    var isSelected : Boolean? = false
)
