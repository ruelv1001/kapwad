package dswd.ziacare.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class SendPointsToUserRequest(
    val amount: String,
    val user_id: String? = null,
    val group_id: String? = null,
    val notes: String? = null
)