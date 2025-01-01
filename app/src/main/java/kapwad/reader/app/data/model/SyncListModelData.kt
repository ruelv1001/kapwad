package kapwad.reader.app.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ProductListModelData(
    var id: String?=null,
    var title: String?=null,
    var name : String? =null,
    var avatar : String? =null,
    var amount : String? =null,
    var status : String? =null,
    var quantity : String? =null
): Parcelable
