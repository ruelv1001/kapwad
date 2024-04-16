package dswd.ziacare.app.data.repositories.auth.request

data class DeleteOrDeactivateRequest(
    var reason_id : String,
    var other_reason: String? = null,
    var type : String,
    var otp: String? = null
)
