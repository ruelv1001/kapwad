package com.lionscare.app.data.repositories.wallet

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import javax.inject.Inject


class WalletPagingSource @Inject constructor(private val walletRemoteDataSource: WalletRemoteDataSource) :
    PagingSource<Int, TransactionData>() {

    override fun getRefreshKey(state: PagingState<Int, TransactionData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionData> {
        val page = params.key ?: 1

        return try {
            val response = walletRemoteDataSource.getWalletTransaction(page.toString(), params.loadSize.toString())
            if(response.data?.isNotEmpty() == true){
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if ((response.total ?: 0) > page) page + 1 else null
                )
            }else{
                LoadResult.Error(NoSuchElementException("No more data available"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}