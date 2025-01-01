package kapwad.reader.app.data.repositories.group.request


import androidx.annotation.Keep

@Keep
data class GetGroupListRequest(
    var page: Int? = 0,
    var per_page: Int? = 0
)