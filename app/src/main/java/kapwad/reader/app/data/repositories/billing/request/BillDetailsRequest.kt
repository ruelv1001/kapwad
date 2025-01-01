package kapwad.reader.app.data.repositories.billing.request


import androidx.annotation.Keep

@Keep
data class BillDetailsRequest(
    var code: String? = null,
    val include: String = "attachment"
)