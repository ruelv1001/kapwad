package kapwad.reader.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
class TransactionListRequest(
    val per_page: String? = null,
    var page: String? = null,
    var group_id: String? = null
)