package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize

@Entity(tableName = "tbl_consumersaccounttb")
data class ConsumerListModelData(
    val id: String,
    val is_employee: String? = null,
    val Arrears_Deduct: String? = null,
    val Arrears_Description: String? = null,
    val Date_Arrears: String? = null,
    val Date_Others: String? = null,
    val Others_Deduct: String? = null,
    val Others_Description: String? = null,
    val Stag_Arrears_Prev: String? = null,
    val Stag_Others_Prev: String? = null,
    val accountnumber: String? = null,
    val address: String? = null,
    val amount: Int? = null,
    val amount_balance: String? = null,
    val barangay: String? = null,
    val brand: String? = null,
    val class_type: String? = null,
    @PrimaryKey
    val consumersid: Int? = 0,
    val convenience_fee: String? = null,
    val cpnumber: String? = null,
    val date: String? = null,
    val dateorientation: String? = null,
    val email: String? = null,
    val end_date: String? = null,
    val firstname: String? = null,
    val firstreading: String? = null,
    val lastname: String? = null,
    val meternumber: String? = null,
    val middlename: String? = null,
    val month: String? = null,
    val num_of_months: String? = null,
    val ornumber: String? = null,
    val password: String? = null,
    val senior_citizen_rate: String? = null,
    val start_date: String? = null,
    val status: String? = null,
    val teller: String? = null,
    val tin_number: String? = null,
    val valid_id_number: String? = null,
    val waterrate: String? = null,
    val zone: String? = null,
): Parcelable


data class ConsumerListResponse(
    val consumers: List<ConsumerListModelData>
)