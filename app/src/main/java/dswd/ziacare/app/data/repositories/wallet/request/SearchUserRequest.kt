package dswd.ziacare.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class SearchUserRequest(
    val keyword: String? = null
)