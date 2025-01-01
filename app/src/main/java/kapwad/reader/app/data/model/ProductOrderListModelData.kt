package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "tbl_order")
data class ProductOrderListModelData(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    val product_id: String,
    val name: String,
    val amount: String?,
    val quantity: String?
): Parcelable
