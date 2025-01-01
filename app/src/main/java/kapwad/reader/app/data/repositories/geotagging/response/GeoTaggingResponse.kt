package kapwad.reader.app.data.repositories.geotagging.response


import androidx.annotation.Keep

@Keep
data class GeoTaggingResponse(
    val `data`: GeoTaggingData? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null,
)

@Keep
data class GeoTaggingData(
    val approved_at: ApprovedAt? = null,
    val approver_name: String? = null,
    val created_at: CreatedAt? = null,
    val geotagging_image: GeotaggingImage? = null,
    val id: Int? = null,
    val land_area: String? = null,
    val status: String? = null,
    val updated_at: UpdatedAt? = null,
    val uploaded_at: UploadedAt? = null,
    val user_id: String? = null,
)

@Keep
data class ApprovedAt(
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
data class GeotaggingImage(
    val directory: String? = null,
    val filename: String? = null,
    val full_path: String? = null,
    val path: String? = null,
    val thumb_path: String? = null,
)

@Keep
data class UpdatedAt(
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
data class UploadedAt(
    val date_db: String? = null,
    val date_only: String? = null,
    val date_only_ph: String? = null,
    val datetime_ph: String? = null,
    val iso_format: String? = null,
    val month_year: String? = null,
    val time_passed: String? = null,
    val timestamp: String? = null,
)


