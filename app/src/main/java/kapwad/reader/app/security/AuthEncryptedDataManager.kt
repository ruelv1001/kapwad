package kapwad.reader.app.security

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kapwad.reader.app.base.CommonsLib
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.repositories.baseresponse.Avatar
import kapwad.reader.app.data.repositories.baseresponse.DateModel
import kapwad.reader.app.data.repositories.baseresponse.QrValue
import kapwad.reader.app.data.repositories.baseresponse.UserModel

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

/*SETTER FOR READING DATE*/

    fun setMonthYear(monthyear: String) {
        sharedPreferences.edit(true) {
            putString(MONTH_YEAR, monthyear)
        }
    }

    fun setLogin(isLogin: String) {
        sharedPreferences.edit(true) {
            putString(IS_LOGIN, isLogin)
        }
    }
    fun setReadingDate(readingdate: String) {
        sharedPreferences.edit(true) {
            putString(READING_DATE, readingdate)
        }
    }
    fun setDueDate(duedate: String) {
        sharedPreferences.edit(true) {
            putString(DUE_DATE, duedate)
        }
    }
    fun setDiscoDate(discodate: String) {
        sharedPreferences.edit(true) {
            putString(DISCO_DATE, discodate)
        }
    }
    fun setBackDate(backdate: String) {
        sharedPreferences.edit(true) {
            putString(BACK_DATE, backdate)
        }
    }

    /**
     * Function used to get the user access token in this sharedPref
     */
    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""

    fun getMonthYear() = sharedPreferences.getString(MONTH_YEAR, "") ?: ""
    fun getIsLogin() = sharedPreferences.getString(IS_LOGIN, "") ?: ""
    fun getReadingDate() = sharedPreferences.getString(READING_DATE, "") ?: ""
    fun getDueDate() = sharedPreferences.getString(DUE_DATE, "") ?: ""
    fun getDiscoDate() = sharedPreferences.getString(DISCO_DATE, "") ?: ""
    fun getBackDate() = sharedPreferences.getString(BACK_DATE, "") ?: ""

    private var inMemoryUserData: MeterReaderListModelData? = null
    private var inMemoryDateRegisteredData: DateModel? = null
    private var inMemoryQRValueData: QrValue? = null
    private var inMemoryAvatarData: Avatar? = null
    private var inMemoryKYCStatusData: String? = null

    /**
     * Function to set user's basic info
     */
    fun setUserBasicInfo(userInfo: MeterReaderListModelData) {
        inMemoryUserData = userInfo


        sharedPreferences.edit(true) {
            putString(MRID, userInfo.mrid.toString())
            putString(DATE, userInfo.date.toString())
            putString(FIRSTNAME, userInfo.firstname.toString())
            putString(MIDDLENAME, userInfo.middlename.toString())
            putString(LASTNAME, userInfo.lastname.toString())
            putString(USERNAME, userInfo.username.toString())
            putString(PASSWORD, userInfo.password.toString())


        }
    }

    /**
     * Function to set user's date created info
     */

    fun setUserQRValue(dataValue: QrValue) {
        inMemoryQRValueData = dataValue
        sharedPreferences.edit(true) {
            putString(USER_QR_VAL_TYPE, dataValue.type)
            putString(USER_QR_VAL_VALUE, dataValue.value)
            putString(USER_QR_VAL_SIGNATURE, dataValue.signature)
        }
    }




    /**
     * Function used to get user's basic info
     */
    fun getUserBasicInfo(): MeterReaderListModelData {
        if (inMemoryUserData == null) {
            inMemoryUserData = MeterReaderListModelData().apply {
                mrid = sharedPreferences.getInt(MRID, 0)
                date = sharedPreferences.getString(DATE, "")
                firstname = sharedPreferences.getString(FIRSTNAME, "")
                middlename = sharedPreferences.getString(MIDDLENAME, "")
                lastname = sharedPreferences.getString(LASTNAME, "")
                username = sharedPreferences.getString(USERNAME, "")
                password = sharedPreferences.getString(PASSWORD, "")

            }
        }
        return inMemoryUserData ?: MeterReaderListModelData()
    }

    /**
     * Function used to get user's date created info
     */





    fun isLoggedIn(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    /**
     * Function used to clear all saved user info in this sharedPref
     * commonly used after success logout
     */
    fun clearUserInfo(){
        inMemoryUserData =MeterReaderListModelData()
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

        private const val MRID = "MRID"
        private const val DATE = "DATE"
        private const val FIRSTNAME = "FIRSTNAME"
        private const val MIDDLENAME = "MIDDLENAME  "
        private const val LASTNAME = "LASTNAME"
        private const val USERNAME = "USERNAME"
        private const val PASSWORD = "password"


        /*FOR READING DATE CODE QUICK DATABASE*/
        private const val MONTH_YEAR = "MONTHYEAR"
        private const val READING_DATE = "READING_DATE"
        private const val DUE_DATE = "DUE_DATE"
        private const val DISCO_DATE = "DISCO_DATE"
        private const val BACK_DATE = "BACK_DATE"
        private const val IS_LOGIN = "MONTHYEAR"

    }

}