package kapwad.reader.app.data.repositories.wallet.request


import androidx.annotation.Keep

@Keep
data class ScanQRRequest(
    val user_qrcode: String
)