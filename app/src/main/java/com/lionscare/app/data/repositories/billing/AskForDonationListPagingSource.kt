package com.lionscare.app.data.repositories.billing

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lionscare.app.data.repositories.billing.response.BillData
import javax.inject.Inject

class AskForDonationListPagingSource @Inject constructor(private val billRemoteDataSource: BillRemoteDataSource) :
    PagingSource<Int, BillData>() {
    override fun getRefreshKey(state: PagingState<Int, BillData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BillData> {
        val page = params.key ?: 1

        return try {
            val response = billRemoteDataSource.doGetAskForDonationList(page.toString(), params.loadSize.toString())
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