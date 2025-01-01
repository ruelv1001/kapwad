package kapwad.reader.app.data.repositories.crops.response


import androidx.annotation.Keep

@Keep
data class CropItemListResponse(
    val `data`: List<CropItemData>? = null,

    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null,
)

@Keep
data class CropItemData(
    val created_at: CreatedAt? = null,
    val date: Date? = null,
    val id: Int? = null,
    val status: String? = null,
    val update_eligible: Boolean? = null,
)

@Keep
data class CreatedAt(
    val date_db: String? = null,
    val date_only: String? = null,
    val date_only_ph: String? = null,
    val datetime_ph: String? = null,
    val iso_format: String? = null,
    val month_year: String? = null,
    val time_passed: String? = null,
    val timestamp: String? = null,
)

@Keep
data class Date(
    val date_db: String? = null,
    val date_only: String? = null,
    val date_only_ph: String? = null,
    val datetime_ph: String? = null,
    val iso_format: String? = null,
    val month_year: String? = null,
    val time_passed: String? = null,
    val timestamp: String? = null,
)


