package kapwad.reader.app.data.repositories.billing.response


import androidx.annotation.Keep
import kapwad.reader.app.data.repositories.baseresponse.DateModel
import kapwad.reader.app.data.repositories.baseresponse.Meta
import kapwad.reader.app.data.repositories.baseresponse.QrValue

@Keep
data class BillListResponse(
    var data: List<BillData>? = null,
    var has_morepages: Boolean? = false,
    var meta: Meta? = null,
    var msg: String? = null,
    var status: Boolean? = null,
    var status_code: String? = null,
    var total: Int? = 0,
    var total_page: Int? = 0
)

@Keep
data class BillData(
    var amount: String? = null,
    var code: String? = null,
    var date_created: DateModel? = null,
    var display_amount: String? = null,
    var display_donated_amount: String? = null,
    var donated_amount: String? = null,
    var due_date: DateModel? = null,
    var id: Int? = 0,
    var name: String? = null,
    var qr_value: QrValue? = null,
    var qrcode_value: String? = null,
    var remarks: String? = null,
    var requests_group: Int? = null,
    var requests_user: Int? = null,
    var status: String? = null,
    var type: String? = null
)