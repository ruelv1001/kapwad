package da.farmer.app.data.repositories.article.response

import da.farmer.app.data.repositories.baseresponse.DateModel
import da.farmer.app.data.repositories.baseresponse.ImageModel


data class ArticleResponse(
    val data: ArticleData? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null
)

data class ArticleData(
    val article_id: Int? = null,
    val date_created: DateModel? = null,
    val description: String? = null,
    val image: ImageModel? = null,
    val name: String? = null,
    val type: String? = null,
    val reference: String? = null
)