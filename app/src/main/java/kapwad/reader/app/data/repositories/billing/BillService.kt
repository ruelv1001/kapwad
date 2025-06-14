package kapwad.reader.app.data.repositories.billing

import kapwad.reader.app.data.repositories.baseresponse.GeneralResponse
import kapwad.reader.app.data.repositories.billing.request.AskDonationRequest
import kapwad.reader.app.data.repositories.billing.request.GetListOfAskedDonationRequest
import kapwad.reader.app.data.repositories.billing.request.BillDetailsRequest
import kapwad.reader.app.data.repositories.billing.request.MyBillListRequest
import kapwad.reader.app.data.repositories.billing.response.BillDetailsResponse
import kapwad.reader.app.data.repositories.billing.response.BillListResponse
import kapwad.reader.app.data.repositories.group.request.GetGroupListRequest
import kapwad.reader.app.data.repositories.group.response.GetGroupListResponse
import kapwad.reader.app.data.repositories.member.response.ListOfMembersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BillService {

    //bulletin
    @POST("api/bills/bulletin/all")
    suspend fun doGetAllBillList(@Body getGroupListRequest: GetGroupListRequest): Response<BillListResponse>
    @POST("api/bills/bulletin/requests")
    suspend fun doGetAskForDonationList(@Body getGroupListRequest: GetGroupListRequest): Response<BillListResponse>


    //=============Ask for donation
    @POST("api/bills/requests/groups-list")
    suspend fun doGetAllListOfGroupRequestedForDonations(@Body getListOfAskedDonationRequest: GetListOfAskedDonationRequest): Response<GetGroupListResponse>
    @POST("api/bills/requests/users-list")
    suspend fun doGetAllListOfUserRequestedForDonations(@Body getListOfAskedDonationRequest: GetListOfAskedDonationRequest): Response<ListOfMembersResponse>

    @POST("api/bills/requests/users")
    suspend fun doRequestToUsers(@Body askDonationRequest: AskDonationRequest): Response<GeneralResponse>
    @POST("api/bills/requests/users")
    suspend fun doRequestToGroups(@Body askDonationRequest: AskDonationRequest): Response<GeneralResponse>


    @POST("api/bills/all")
    suspend fun doGetAllMyBillList(@Body myBillListRequest: MyBillListRequest): Response<BillListResponse>
    @POST("api/bills/show")
    suspend fun doGetBillDetails(@Body billDetailsRequest: BillDetailsRequest): Response<BillDetailsResponse>

}