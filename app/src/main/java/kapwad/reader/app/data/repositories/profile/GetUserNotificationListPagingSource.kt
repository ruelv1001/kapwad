package kapwad.reader.app.data.repositories.profile

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kapwad.reader.app.data.repositories.profile.response.UserNotificationData
import javax.inject.Inject

class GetUserNotificationListPagingSource @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource) :
    PagingSource<Int, UserNotificationData>() {
    override fun getRefreshKey(state: PagingState<Int, UserNotificationData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserNotificationData> {
        val page = params.key ?: 1

        return try {
            val response = profileRemoteDataSource.getUserNotificationList(
                per_page = params.loadSize,
                page = page
            )
            if (response.data?.isNotEmpty() == true) {
                LoadResult.Page(
                    data = response.data.orEmpty(),
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if ((response.total ?: 0) > page) page + 1 else null
                )
            } else {
                LoadResult.Error(NoSuchElementException("No more data available"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
