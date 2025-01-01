package kapwad.reader.app.data.repositories


import android.support.annotation.Keep

class TempList : ArrayList<TempListItem>(){
    @Keep
    data class TempListItem(
        val Address: String,
        val Amount_Balance: String,
        val Arrears_Deduct: String,
        val Arrears_Description: String,
        val Brand: String,
        val Concessionaire: String,
        val Date_Arrears: String,
        val Date_Others: String,
        val Meternumber: String,
        val Num_of_Months: String,
        val Others_Deduct: String,
        val Others_Description: String,
        val Prev: String,
        val Service_Status: String,
        val Stag_Arrears_Prev: String,
        val Stag_Others_Prev: String,
        val account_number: String,
        val account_series: String,
        val date_read_pay: String,
        val id: Int,
        val status_type: String,
        val zone: String
    )
}