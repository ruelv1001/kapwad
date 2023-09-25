package com.lionscare.app.data.repositories.member

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.User
import com.lionscare.app.utils.CommonLogger

class GetListOfMembersPagingSource constructor(
    private val memberRemoteDataSource: MemberRemoteDataSource,
    private val groupId: String? = null,
    private val include_admin: Boolean? = null,
    private val ownerInfo: UserModel? = null
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
            val response = memberRemoteDataSource.doGetListOfMembers(
                groupId = groupId,
                include_admin = include_admin,
                page = page.toString()
            )
            if (response.data?.isNotEmpty() == true) {
                if (page == 1 && include_admin == true){
                    val newListData  = response.data?.toMutableList()
                    newListData?.add(0, ownerInfo())

                    LoadResult.Page(
                        data = newListData.orEmpty(),
                        prevKey = if (page > 1) page - 1 else null,
                        nextKey = if ((response.total ?: 0) > page) page + 1 else null
                    )
                }else{
                    LoadResult.Page(
                        data = response.data.orEmpty(),
                        prevKey = if (page > 1) page - 1 else null,
                        nextKey = if ((response.total ?: 0) > page) page + 1 else null
                    )
                }

            } else {
                if(page == 1 && include_admin == true){
                    val newListData  = response.data?.toMutableList()
                    newListData?.add(0, ownerInfo())

                    LoadResult.Page(
                        data = newListData.orEmpty(),
                        prevKey = if (page > 1) page - 1 else null,
                        nextKey = if ((response.total ?: 0) > page) page + 1 else null
                    )
                }else{
                    LoadResult.Error(NoSuchElementException("No more data available"))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun ownerInfo() : MemberListData{
        val userInfo = ownerInfo
        val userModel = User(
            avatar = ownerInfo?.avatar,
            name = userInfo?.name,
            qrcode = userInfo?.qrcode,
            id = userInfo?.id
        )
        return MemberListData(
            user = userModel,
            role = "owner",
            id = 0
        )
    }
}
