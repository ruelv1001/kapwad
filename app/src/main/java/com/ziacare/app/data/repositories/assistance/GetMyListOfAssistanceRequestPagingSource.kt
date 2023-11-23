package com.ziacare.app.data.repositories.assistance

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ziacare.app.data.repositories.assistance.response.CreateAssistanceData
import com.ziacare.app.data.repositories.member.response.MemberListData
import javax.inject.Inject

class GetMyListOfAssistanceRequestPagingSource @Inject constructor(
    private val assistanceRequestRemoteDataSource: AssistanceRemoteDataSource,
    private val groupId: String,
    private val filter: List<String>
) :
    PagingSource<Int, CreateAssistanceData>() {
    override fun getRefreshKey(state: PagingState<Int, CreateAssistanceData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreateAssistanceData> {
        val page = params.key ?: 1

        return try {
            val response = assistanceRequestRemoteDataSource.doGetMyAssistanceRequestList(
                groupId = groupId,
                per_page = params.loadSize,
                page = page,
                filter = filter
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
