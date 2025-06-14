package kapwad.reader.app.data.repositories.wallet

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kapwad.reader.app.data.repositories.wallet.response.TransactionData


class GroupWalletPagingSource(
    private val walletRemoteDataSource: GroupWalletRemoteDataSource,
    private val groupId: String
) : PagingSource<Int, TransactionData>() {

    override fun getRefreshKey(state: PagingState<Int, TransactionData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionData> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = walletRemoteDataSource.getWalletTransaction(
                page.toString(),
                params.loadSize.toString(),
                groupId
            )
            if(response.data?.isNotEmpty() == true){
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                    nextKey = if (response.data.isEmpty()) null else page.plus(1)
                )
            }else{
                LoadResult.Error(NoSuchElementException("No more data available"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}