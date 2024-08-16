package da.farmer.app.data.repositories.billing.response


import androidx.annotation.Keep
import da.farmer.app.data.repositories.baseresponse.DateModel
import da.farmer.app.data.repositories.baseresponse.Meta
import da.farmer.app.data.repositories.baseresponse.QrValue

@Keep
data class BillDetailsResponse(
    var data: BillDetailsData? = null,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = false,
    var status_code: String? = null
)

@Keep
data class BillDetailsData(
    var amount: String? = null,
    var amount_paid: String? = null,
    var attachment: List<Attachment>? = null,
    var balance: Double? = 0.0,
    var cancelled_date: DateModel? = null,
    var code: String? = null,
    var date_created: DateModel? = null,
    var display_amount: String? = null,
    var display_amount_paid: String? = null,
    var display_balance: String? = null,
    var display_donated_amount: String? = null,
    var donated_amount: Double? = 0.0,
    var due_date: DateModel? = null,
    var id: Int? = 0,
    var name: String? = null,
    var payment_date: DateModel? = null,
    var qr_value: QrValue? = null,
    var qrcode_value: String? = null,
    var remarks: String? = null,
    var requests_group: Int? = 0,
    var requests_user: Int? = 0,
    var status: String? = null,
    var type: String? = null
)

@Keep
data class Attachment(
    var bill_id: Int? = 0,
    var directory: String? = null,
    var filename: String? = null,
    var full_path: String? = null,
    var id: Int? = 0,
    var path: String? = null,
    var type: String? = null
)

