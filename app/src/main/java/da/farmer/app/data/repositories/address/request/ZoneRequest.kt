package da.farmer.app.data.repositories.address.request


import androidx.annotation.Keep

@Keep
data class ZoneRequest(
    var region_id: String? = null
)