package da.farmer.app.data.repositories.group

import androidx.paging.PagingSource
import androidx.paging.PagingState
import da.farmer.app.data.repositories.group.response.GroupListData
import javax.inject.Inject

class GetGroupPagingSource @Inject constructor(private val groupRemoteDataSource: GroupRemoteDataSource) :
    PagingSource<Int, GroupListData>() {
    override fun getRefreshKey(state: PagingState<Int, GroupListData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupListData> {
        val page = params.key ?: 1

        return try {
            val response = groupRemoteDataSource.doGetGroupList(page.toString(), params.loadSize.toString())
            if(response.data?.isNotEmpty() == true){
                LoadResult.Page(
                    data = response.data.orEmpty(),
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