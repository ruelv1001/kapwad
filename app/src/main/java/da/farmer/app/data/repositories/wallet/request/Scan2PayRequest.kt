package da.farmer.app.data.repositories.wallet.request

import androidx.annotation.Keep

@Keep
data class Scan2PayRequest(
    val amount: String,
    val mid: String,
    val remarks: String
)