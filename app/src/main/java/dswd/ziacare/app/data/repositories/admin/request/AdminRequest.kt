package dswd.ziacare.app.data.repositories.admin.request

import androidx.annotation.Keep

@Keep
data class AdminRequest(
    val member_id: Int? = null,
    val group_id: String? = null,
    val user_id: String? = null
)