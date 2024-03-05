package dswd.ziacare.app.data.repositories.baseresponse

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class DateModel(
    var date_db: String? = null,
    var date_only: String? = null,
    var datetime_ph: String? = null,
    var date_only_ph: String? = null,
    var time_passed: String? = null,
    var timestamp: String? = null,
    var iso_format: String? = null,
    var month_year: String? = null
) : Serializable, Parcelable
