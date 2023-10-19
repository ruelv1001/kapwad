package com.lionscare.app.security

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lionscare.app.base.CommonsLib
import com.lionscare.app.data.repositories.baseresponse.Avatar
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.QrValue
import com.lionscare.app.data.repositories.baseresponse.UserModel

class AuthEncryptedDataManager {

    private val keyGenParameterSpec by lazy {
        KeyGenParameterSpec.Builder(
            ENCRYPTED_ALIAS_NAME,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
    }

    private val masterKeyAlias by lazy {
        MasterKey.Builder(CommonsLib.context!!, ENCRYPTED_ALIAS_NAME)
            .setKeyGenParameterSpec(keyGenParameterSpec)
            .build()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            CommonsLib.context!!,
            ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    /**
     * Function used to save the user access token in this sharedPref
     */
    fun setAccessToken(token: String) {
        sharedPreferences.edit(true) {
            putString(ACCESS_TOKEN, token)
        }
    }

    /**
     * Function used to get the user access token in this sharedPref
     */
    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""

    private var inMemoryUserData: UserModel? = null
    private var inMemoryDateRegisteredData: DateModel? = null
    private var inMemoryQRValueData: QrValue? = null
    private var inMemoryAvatarData: Avatar? = null
    private var inMemoryKYCStatusData: String? = null

    /**
     * Function to set user's basic info
     */
    fun setUserBasicInfo(userInfo: UserModel) {
        inMemoryUserData = userInfo

        setUserDateInfo(userInfo.date_registered?: DateModel())
        setUserQRValue(userInfo.qr_value?: QrValue())
        setUserAvatar(userInfo.avatar?: Avatar())

        sharedPreferences.edit(true) {
            putString(USER_INFO_ID, userInfo.id)
            putString(USER_QRCODE_VALUE, userInfo.qrcode_value)
            putString(USER_NAME, userInfo.name)
            putString(USER_FIRST_NAME, userInfo.firstname)
            putString(USER_MIDDLE_NAME, userInfo.middlename)
            putString(USER_LAST_NAME, userInfo.lastname)
            putString(USER_EMAIL, userInfo.email)
            putString(USER_PHONE_NUMBER, userInfo.phone_number)
            putString(USER_ADDRESS, userInfo.address)
            putString(USER_PROVINCE_NAME, userInfo.province_name)
            putString(USER_PROVINCE_SKU, userInfo.province_sku)
            putString(USER_CITY_NAME, userInfo.city_name)
            putString(USER_CITY_CODE, userInfo.city_code)
            putString(USER_BRGY_NAME, userInfo.brgy_name)
            putString(USER_BRGY_CODE, userInfo.brgy_code)
            putString(USER_QRCODE, userInfo.qrcode)
            putString(USER_ZIPCODE, userInfo.zipcode)
            putString(USER_KYC_STATUS, userInfo.kyc_status)
            putBoolean(USER_IS_COMPLETE_PROFILE, userInfo.is_complete_profile?: false)
        }
    }

    /**
     * Function to set user's date created info
     */
    fun setUserDateInfo(dateInfo: DateModel) {
        inMemoryDateRegisteredData = dateInfo
        sharedPreferences.edit(true) {
            putString(USER_DATE_DB, dateInfo.date_db)
            putString(USER_DATE_ONLY, dateInfo.date_only)
            putString(USER_DATE_TIME_PASSED, dateInfo.time_passed)
            putString(USER_DATE_TIMESTAMP, dateInfo.timestamp)
            putString(USER_DATE_ISO_FORMAT, dateInfo.iso_format)
            putString(USER_DATE_MONTH_YEAR, dateInfo.month_year)
        }
    }

    fun setUserQRValue(dataValue: QrValue) {
        inMemoryQRValueData = dataValue
        sharedPreferences.edit(true) {
            putString(USER_QR_VAL_TYPE, dataValue.type)
            putString(USER_QR_VAL_VALUE, dataValue.value)
            putString(USER_QR_VAL_SIGNATURE, dataValue.signature)
        }
    }

    fun setUserAvatar(dataValue: Avatar) {
        inMemoryAvatarData = dataValue
        sharedPreferences.edit(true) {
            putString(USER_AVATAR_FILENAME, dataValue.filename)
            putString(USER_AVATAR_PATH, dataValue.path)
            putString(USER_AVATAR_DIRECTORY, dataValue.directory)
            putString(USER_AVATAR_FULL_PATH, dataValue.full_path)
            putString(USER_AVATAR_THUMB_PATH, dataValue.thumb_path)
        }
    }

    fun setUserKYCStatus(kycStatus : String) {
        inMemoryKYCStatusData = kycStatus
        sharedPreferences.edit(true) {
            putString(USER_KYC_STATUS, kycStatus)
        }
    }

    /**
     * Function used to get user's basic info
     */
    fun getUserBasicInfo(): UserModel {
        if (inMemoryUserData == null) {
            inMemoryUserData = UserModel().apply {
                id = sharedPreferences.getString(USER_INFO_ID, "")
                qrcode_value = sharedPreferences.getString(USER_QRCODE_VALUE, "")
                qrcode = sharedPreferences.getString(USER_QRCODE, "")
                name = sharedPreferences.getString(USER_NAME, "")
                firstname = sharedPreferences.getString(USER_FIRST_NAME, "")
                middlename = sharedPreferences.getString(USER_MIDDLE_NAME, "")
                lastname = sharedPreferences.getString(USER_LAST_NAME, "")
                email = sharedPreferences.getString(USER_EMAIL, "")
                phone_number = sharedPreferences.getString(USER_PHONE_NUMBER, "")
                address = sharedPreferences.getString(USER_ADDRESS, "")
                province_name = sharedPreferences.getString(USER_PROVINCE_NAME, "")
                province_sku = sharedPreferences.getString(USER_PROVINCE_SKU, "")
                city_name = sharedPreferences.getString(USER_CITY_NAME, "")
                city_code = sharedPreferences.getString(USER_CITY_CODE, "")
                brgy_name = sharedPreferences.getString(USER_BRGY_NAME, "")
                brgy_code = sharedPreferences.getString(USER_BRGY_CODE, "")
                zipcode = sharedPreferences.getString(USER_ZIPCODE, "")
                kyc_status = sharedPreferences.getString(USER_KYC_STATUS, "")
                is_complete_profile = sharedPreferences.getBoolean(USER_IS_COMPLETE_PROFILE, false)
            }
        }
        return inMemoryUserData ?: UserModel()
    }

    /**
     * Function used to get user's date created info
     */
    fun getUserDateInfo(): DateModel {
        if (inMemoryDateRegisteredData == null) {
            inMemoryDateRegisteredData = DateModel().apply {
                date_db = sharedPreferences.getString(USER_DATE_DB, "")
                date_only = sharedPreferences.getString(USER_DATE_ONLY, "")
                time_passed = sharedPreferences.getString(USER_DATE_TIME_PASSED, "")
                timestamp = sharedPreferences.getString(USER_DATE_TIMESTAMP, "")
                iso_format = sharedPreferences.getString(USER_DATE_ISO_FORMAT, "")
                month_year = sharedPreferences.getString(USER_DATE_MONTH_YEAR, "")
            }
        }
        return inMemoryDateRegisteredData ?: DateModel()
    }
    fun getUserQRValue(): QrValue {
        if (inMemoryQRValueData == null) {
            inMemoryQRValueData = QrValue().apply {
                type = sharedPreferences.getString(USER_QR_VAL_TYPE, "")
                value = sharedPreferences.getString(USER_QR_VAL_VALUE, "")
                signature = sharedPreferences.getString(USER_QR_VAL_SIGNATURE, "")
            }
        }
        return inMemoryQRValueData ?: QrValue()
    }
    fun getUserAvatar(): Avatar {
        if (inMemoryAvatarData == null) {
            inMemoryAvatarData = Avatar().apply {
                filename = sharedPreferences.getString(USER_AVATAR_FILENAME, "")
                path = sharedPreferences.getString(USER_AVATAR_PATH, "")
                directory = sharedPreferences.getString(USER_AVATAR_DIRECTORY, "")
                full_path = sharedPreferences.getString(USER_AVATAR_FULL_PATH, "")
                thumb_path = sharedPreferences.getString(USER_AVATAR_THUMB_PATH, "")
            }
        }
        return inMemoryAvatarData ?: Avatar()
    }

    fun getKYCStatus(): String {
        if (inMemoryKYCStatusData == null) {
            inMemoryKYCStatusData = sharedPreferences.getString(USER_KYC_STATUS, "")
        }
        return inMemoryKYCStatusData ?: ""
    }


    fun isLoggedIn(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    /**
     * Function used to clear all saved user info in this sharedPref
     * commonly used after success logout
     */
    fun clearUserInfo(){
        inMemoryUserData = UserModel()
        inMemoryDateRegisteredData = DateModel()
        inMemoryQRValueData = QrValue()
        inMemoryAvatarData = Avatar()
        inMemoryKYCStatusData = ""
        setAccessToken("")
    }

    //TODO Don't use Clear, Buggy on Library's current version
    fun clear() {
        sharedPreferences.edit(true) {
            clear()
            commit()
        }
    }

    /**
     * Function used to clear saved accessToken in this sharedPref
     * commonly used after success logout
     */
    fun resetToken() {
        setAccessToken("")
    }

    companion object{
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val ENCRYPTED_PREFS_NAME = "ENCRYPTED_PREFS_NAME"
        private const val ENCRYPTED_ALIAS_NAME = "ENCRYPTED_ALIAS_NAME"

        private const val USER_QR_VAL_TYPE = "USER_QR_VAL_TYPE"
        private const val USER_QR_VAL_VALUE = "USER_QR_VAL_VALUE"
        private const val USER_QR_VAL_SIGNATURE = "USER_QR_VAL_SIGNATURE"

        private const val USER_INFO_ID = "USER_INFO_ID"
        private const val USER_QRCODE_VALUE = "USER_QRCODE_VALUE"
        private const val USER_QRCODE = "USER_QRCODE"
        private const val USER_NAME = "USER_NAME"
        private const val USER_FIRST_NAME = "USER_FIRST_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_MIDDLE_NAME = "USER_MIDDLE_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_PHONE_NUMBER = "USER_PHONE_NUMBER"
        private const val USER_ADDRESS = "USER_ADDRESS"
        private const val USER_PROVINCE_SKU = "USER_PROVINCE_SKU"
        private const val USER_PROVINCE_NAME = "USER_PROVINCE_NAME"
        private const val USER_CITY_CODE = "USER_CITY_CODE"
        private const val USER_CITY_NAME = "USER_CITY_NAME"
        private const val USER_BRGY_CODE = "USER_BRGY_CODE"
        private const val USER_BRGY_NAME = "USER_BRGY_NAME"
        private const val USER_ZIPCODE = "USER_ZIPCODE"
        private const val USER_KYC_STATUS = "USER_KYC_STATUS"
        private const val USER_IS_COMPLETE_PROFILE = "USER_IS_COMPLETE_PROFILE"

        private const val USER_AVATAR_FILENAME = "USER_AVATAR_FILENAME"
        private const val USER_AVATAR_PATH = "USER_AVATAR_PATH"
        private const val USER_AVATAR_DIRECTORY = "USER_AVATAR_DIRECTORY"
        private const val USER_AVATAR_FULL_PATH = "USER_AVATAR_FULL_PATH"
        private const val USER_AVATAR_THUMB_PATH = "USER_AVATAR_THUMB_PATH"

        private const val USER_DATE_DB = "USER_DATE_DB"
        private const val USER_DATE_MONTH_YEAR = "USER_DATE_MONTH_YEAR"
        private const val USER_DATE_ONLY = "USER_DATE_ONLY"
        private const val USER_DATE_ISO_FORMAT = "USER_DATE_ISO_FORMAT"
        private const val USER_DATE_TIME_PASSED = "USER_DATE_TIME_PASSED"
        private const val USER_DATE_TIMESTAMP = "USER_DATE_TIMESTAMP"

    }

}