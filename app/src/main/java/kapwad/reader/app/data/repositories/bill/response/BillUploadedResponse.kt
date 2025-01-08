package kapwad.reader.app.data.repositories.bill.response

import androidx.annotation.Keep


@Keep
data class BillUploadedResponse(
    val failed_inserts: Int,
    val last_inserted_id: Int,
    val message: String,
    val status: String,
    val successful_inserts: Int
)