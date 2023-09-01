package com.lionscare.app.data.repositories.admin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lionscare.app.data.repositories.member.response.MemberListData
import javax.inject.Inject

class GetListOfAdminPagingSource @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
    private val groupId: String? = null
) :
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
            val response = adminRemoteDataSource.doGetListOfAdmins(
                groupId = groupId,
                page = page.toString()
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
