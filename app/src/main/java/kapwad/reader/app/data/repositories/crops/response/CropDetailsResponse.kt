package kapwad.reader.app.data.repositories.crops.response


import androidx.annotation.Keep

@Keep
data class CropDetailsResponse(
    val `data`: CropDetailsData,
    val msg: String,
    val status: Boolean,
    val status_code: String
)

@Keep
data class CropDetailsData(
    val created_at: CreatedAt,
    val date: Date,
    val id: Int,
    val left_image: LeftImage,
    val left_image_approved_at: LeftImageApprovedAt,
    val left_image_approved_by: String,
    val left_image_status: String,
    val left_image_uploaded_at: LeftImageUploadedAt,
    val left_video: LeftVideo,
    val left_video_approved_at: LeftVideoApprovedAt,
    val left_video_approved_by: String,
    val left_video_status: String,
    val left_video_uploaded_at: LeftVideoUploadedAt,
    val middle_image: MiddleImage,
    val middle_image_approved_at: MiddleImageApprovedAt,
    val middle_image_approved_by: String,
    val middle_image_status: String,
    val middle_image_uploaded_at: MiddleImageUploadedAt,
    val middle_video: MiddleVideo,
    val middle_video_approved_at: MiddleVideoApprovedAt,
    val middle_video_approved_by: String,
    val middle_video_status: String,
    val middle_video_uploaded_at: MiddleVideoUploadedAt,
    val right_image: RightImage,
    val right_image_approved_at: RightImageApprovedAt,
    val right_image_approved_by: String,
    val right_image_status: String,
    val right_image_uploaded_at: RightImageUploadedAt,
    val right_video: RightVideo,
    val right_video_approved_at: RightVideoApprovedAt,
    val right_video_approved_by: String,
    val right_video_status: String,
    val right_video_uploaded_at: RightVideoUploadedAt,
    val status: String
)


@Keep
data class LeftImage(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class LeftImageApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class LeftImageUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class LeftVideo(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class LeftVideoApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class LeftVideoUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class MiddleImage(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class MiddleImageApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class MiddleImageUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class MiddleVideo(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class MiddleVideoApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class MiddleVideoUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class RightImage(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class RightImageApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class RightImageUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class RightVideo(
    val directory: String,
    val filename: String,
    val full_path: String,
    val path: String,
    val thumb_path: String
)

@Keep
data class RightVideoApprovedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val time_passed: String,
    val timestamp: String
)

@Keep
data class RightVideoUploadedAt(
    val date_db: String,
    val date_only: String,
    val date_only_ph: String,
    val datetime_ph: String,
    val iso_format: String,
    val month_year: String,
    val time_passed: String,
    val timestamp: String
)


