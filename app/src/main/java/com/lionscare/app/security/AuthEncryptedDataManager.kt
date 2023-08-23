package com.lionscare.app.security

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.UserModel

class AuthEncryptedDataManager {

    private val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        ENCRYPTED_ALIAS_NAME,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .build()

    private val masterKeyAlias = MasterKey.Builder(com.lionscare.app.base.CommonsLib.context!!, ENCRYPTED_ALIAS_NAME)
        .setKeyGenParameterSpec(keyGenParameterSpec)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        com.lionscare.app.base.CommonsLib.context!!,
        ENCRYPTED_PREFS_NAME,
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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

    /**
     * Function to set user's basic info
     */
    fun setUserBasicInfo(userInfo: UserModel) {
        inMemoryUserData = userInfo

        setUserDateInfo(userInfo.date_registered?: DateModel())

        sharedPreferences.edit(true) {
            putString(USER_INFO_ID, userInfo.id)
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
            putString(USER_ZIPCODE, userInfo.zipcode)
        }
    }

    /**
     * Function used to get user's basic info
     */
    fun getUserBasicInfo(): UserModel {
        if (inMemoryUserData == null) {
            inMemoryUserData = UserModel().apply {
                id = sharedPreferences.getString(USER_INFO_ID, "")
                firstname = sharedPreferences.getString(USER_FIRST_NAME, "")
                middlename = sharedPreferences.getString(USER_MIDDLE_NAME, "0")
                lastname = sharedPreferences.getString(USER_LAST_NAME, "0")
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
            }
        }
        return inMemoryUserData ?: UserModel()
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

        private const val USER_INFO_ID = "USER_INFO_ID"
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

        private const val USER_DATE_DB = "USER_DATE_DB"
        private const val USER_DATE_MONTH_YEAR = "USER_DATE_MONTH_YEAR"
        private const val USER_DATE_ONLY = "USER_DATE_ONLY"
        private const val USER_DATE_ISO_FORMAT = "USER_DATE_ISO_FORMAT"
        private const val USER_DATE_TIME_PASSED = "USER_DATE_TIME_PASSED"
        private const val USER_DATE_TIMESTAMP = "USER_DATE_TIMESTAMP"

    }

}