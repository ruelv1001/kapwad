package kapwad.reader.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class SearchUserRequest(
    val keyword: String? = null
)