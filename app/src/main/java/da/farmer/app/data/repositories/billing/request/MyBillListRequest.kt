package da.farmer.app.data.repositories.billing.request


import androidx.annotation.Keep

@Keep
data class MyBillListRequest(
    var status: String? = null,
    var page: Int? = 0,
    var per_page: Int? = 0
)