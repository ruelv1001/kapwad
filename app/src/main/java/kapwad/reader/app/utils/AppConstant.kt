package kapwad.reader.app.utils

object AppConstant {
    private const val TOKEN_NOT_PROVIDED = "TOKEN_NOT_PROVIDED"
    private const val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    private const val TOKEN_INVALID = "TOKEN_INVALID"
    private const val INVALID_ID_AUTH_USER = "INVALID_ID_AUTH_USER"
    private const val INVALID_TOKEN = "INVALID_TOKEN"
    private const val ACCOUNT_LOGOUT = "ACCOUNT_LOGOUT"
    private const val EXPIRED_TOKEN = "EXPIRED_TOKEN"
    private const val UNAUTHORIZED = "UNAUTHORIZED"


    const val NOT_FOUND = "NOT_FOUND"
    const val NO_IMMEDIATE_FAMILY = "NO_IMMEDIATE_FAMILY"

    private val sessionStatusArray = arrayOf(
        TOKEN_NOT_PROVIDED,
        TOKEN_EXPIRED,
        TOKEN_INVALID,
        INVALID_ID_AUTH_USER,
        INVALID_TOKEN,
        ACCOUNT_LOGOUT,
        EXPIRED_TOKEN,
        UNAUTHORIZED,
    )

    fun isSessionStatusCode(code: String): Boolean{
        return sessionStatusArray.contains(code)
    }
}