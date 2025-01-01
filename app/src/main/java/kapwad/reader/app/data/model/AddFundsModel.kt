package kapwad.reader.app.data.model

import androidx.annotation.Keep

@Keep
data class AddFundsModel(
    var title : String? = null,
    var isSelected : Boolean? = false
)
