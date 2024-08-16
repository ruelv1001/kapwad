package da.farmer.app.data.repositories.billing

import androidx.paging.PagingSource
import androidx.paging.PagingState
import da.farmer.app.data.repositories.billing.response.BillData
import da.farmer.app.data.repositories.group.response.GroupListData
import da.farmer.app.data.repositories.member.response.MemberListData
import javax.inject.Inject

class ListOfUserRequestedForDonationPagingSource @Inject constructor(private val billRemoteDataSource: BillRemoteDataSource, private val code: String) :
    PagingSource<Int, MemberListData>() {
    override fun getRefreshKey(state: PagingState<Int, MemberListData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemberListData> {
        val page = params.key ?: 1

        return try {
            val response = billRemoteDataSource.doGetAllListOfUserRequestedForDonations(page.toString(), params.loadSize.toString(), code = code)
            if(response.data?.isNotEmpty() == true){
                LoadResult.Page(
                    data = response.data.orEmpty(),
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (response.data?.isEmpty() == true) null else page + 1
                )
            }else{
                LoadResult.Error(NoSuchElementException("No more data available"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}