package kapwad.reader.app.data.repositories.auth.request

import androidx.annotation.Keep

@Keep
data class ValidateEmailRequest(
    var email: String
)
