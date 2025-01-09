package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "tbl_bill")
data class CreatedBillListModelData(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var duedate: String? = null,
    var disdate: String? = null,
    val backdate: String? = null,
    val month: String? = null,
    val mrname: String? = null,
    val date : String? = null,
    val pres : String? = null,
    val prev : String? = null,
    val arrears_prev: String? = null,
    val deduct_arrears: String? = null,
    var arrears_pres : String? = null,
    var arrears_date : String? = null,
    var others_prev : String? = null,
    var deduct_others : String? = null,
    var others_pres : String? = null,
    var others_date : String? = null,
    var convenience_fee : String? = null,
    var consume : String? = null,
    var total : String? = null,
    var bill_amount : String? = null,
    var address : String? = null,
    var clas : String? = null,
    var meternumber : String? = null,
    var name : String? = null,
    var accountnumber : String? = null,
    var owners_id : String? = null,
    var amountrate : String? = null,
    var zone : String? = null,
    var barangay : String? = null,
    var senior_citizen_rate : String? = null,
    var wmmf : String? = null,
    var penalty : String? = null,
    var amount_paid : String? = null,
    var date_of_payment : String? = null,
    var paid : String? = null,
    var amount_balance : String? = null,
    var amount_advance : String? = null,
    var teller_name : String? = null,
    var refno : String? = null,
    var Service_Status : String? = null,
    var num_of_months : String? = null,
    var franchise_tax : String? = null,
    var Ftax_total : String? = null,
    var pocatotal : String? = null,
    var image : String? = null,


): Parcelable
