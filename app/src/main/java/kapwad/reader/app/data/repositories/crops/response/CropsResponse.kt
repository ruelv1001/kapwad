package kapwad.reader.app.data.repositories.crops.response

import androidx.annotation.Keep


@Keep
data class CropsResponse(
    val `data`: List<CropsData>? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null,
)

@Keep
data class CropsData(
    val completed_at: CompletedAt? = null,
    val created_at: CreatedAt? = null,
    val id: Int? = null,
    val start_date: StartDate? = null,
    val status: String? = null,
)

@Keep
data class CompletedAt(
    val date_db: String? = null,
    val date_only: String? = null,
    val date_only_ph: String? = null,
    val datetime_ph: String? = null,
    val iso_format: String? = null,
    val time_passed: String? = null,
    val timestamp: String? = null,
)



@Keep
data class StartDate(
    val date_db: String? = null,
    val date_only: String? = null,
    val date_only_ph: String? = null,
    val datetime_ph: String? = null,
    val iso_format: String? = null,
    val month_year: String? = null,
    val time_passed: String? = null,
    val timestamp: String? = null,
)


