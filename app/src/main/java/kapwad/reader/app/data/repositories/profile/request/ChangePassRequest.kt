package kapwad.reader.app.data.repositories.profile.request

import androidx.annotation.Keep

@Keep
data class ChangePassRequest(
    val current_password: String,
    val password: String,
    val password_confirmation: String
)