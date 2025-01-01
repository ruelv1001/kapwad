package kapwad.reader.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class GetGroupBalanceRequest(
    val group_id: String?
)