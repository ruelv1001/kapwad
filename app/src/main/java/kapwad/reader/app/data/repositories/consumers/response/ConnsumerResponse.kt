package kapwad.reader.app.data.repositories.consumers.response

import androidx.annotation.Keep


class ConnsumerResponse : ArrayList<ConnsumerResponse.ConnsumerResponseItem>(){
    @Keep
    data class ConnsumerResponseItem(
        val Arrears_Deduct: String? = null,
        val Arrears_Description: String? = null,
        val Date_Arrears: String? = null,
        val Date_Others: String? = null,
        val Others_Deduct: String? = null,
        val Others_Description: String? = null,
        val Stag_Arrears_Prev: String? = null,
        val Stag_Others_Prev: String? = null,
        val accountnumber: String? = null,
        val address: String? = null,
        val amount: Int? = null,
        val amount_balance: String? = null,
        val barangay: String? = null,
        val brand: String? = null,
        val `class`: String? = null,
        val consumersid: Int? = null,
        val convenience_fee: String? = null,
        val cpnumber: String? = null,
        val date: String? = null,
        val dateorientation: String? = null,
        val email: String? = null,
        val end_date: String? = null,
        val firstname: String? = null,
        val firstreading: String? = null,
        val lastname: String? = null,
        val meternumber: String? = null,
        val middlename: String? = null,
        val month: String? = null,
        val num_of_months: String? = null,
        val ornumber: String? = null,
        val password: String? = null,
        val senior_citizen_rate: String? = null,
        val start_date: String? = null,
        val status: String? = null,
        val teller: String? = null,
        val tin_number: String? = null,
        val valid_id_number: String? = null,
        val waterrate: Int? = null,
        val zone: String? = null,
    )
}