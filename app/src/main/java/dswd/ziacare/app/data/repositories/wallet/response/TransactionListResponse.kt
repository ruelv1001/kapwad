package dswd.ziacare.app.data.repositories.wallet.response


import androidx.annotation.Keep
import dswd.ziacare.app.data.repositories.baseresponse.DateModel

@Keep
data class TransactionListResponse(
    val data: List<TransactionData>? = null,
    val has_morepages: Boolean? = false,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val total: Int? = 0,
    val total_page: Int? = 0
)

@Keep
data class TransactionData(
    val date_registered: DateModel? = null,
    val id: String? = null,
    val remarks: String? = null,
    val title: String? = null,
    val type: String? = null,
    val value: String? = null,
    val notes: String? = null
)