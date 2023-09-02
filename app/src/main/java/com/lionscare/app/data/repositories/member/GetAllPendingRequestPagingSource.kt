package com.lionscare.app.data.repositories.member

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import javax.inject.Inject

class GetAllPendingRequestPagingSource constructor(
    private val memberRemoteDataSource: MemberRemoteDataSource,
    private val groupId: String? = null,
    private val type: String? = null
) :
    PagingSource<Int, PendingMemberData>() {
    override fun getRefreshKey(state: PagingState<Int, PendingMemberData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PendingMemberData> {
        val page = params.key ?: 1

        return try {
            val response = memberRemoteDataSource.doGetAllPendingInviteAndRequest(
                groupId = groupId,
                page = page.toString(),
                type = type
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