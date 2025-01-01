package kapwad.reader.app.data.repositories.crops

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kapwad.reader.app.data.repositories.crops.response.CropsData

import javax.inject.Inject


class CropsPagingSource @Inject constructor(private val cropsRemoteDataSource: CropsRemoteDataSource) :
    PagingSource<Int, CropsData>() {

    override fun getRefreshKey(state: PagingState<Int, CropsData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CropsData> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = cropsRemoteDataSource.doGetAllCropsList()
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