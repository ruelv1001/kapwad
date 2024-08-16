package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.group.GetGroupPagingSource
import da.farmer.app.data.repositories.group.GroupRemoteDataSource
import da.farmer.app.data.repositories.group.GroupRepository
import da.farmer.app.data.repositories.group.GroupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class GroupModule {

    @Provides
    fun providesGroupService(): GroupService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            GroupService::class.java
        )
    }

    @Provides
    fun providesGroupRemoteDataSource(groupService: GroupService): GroupRemoteDataSource {
        return GroupRemoteDataSource(groupService)
    }

    @Provides
    fun providesGetGroupPagingSource(groupRemoteDataSource: GroupRemoteDataSource): GetGroupPagingSource {
        return GetGroupPagingSource(groupRemoteDataSource)
    }

    @Provides
    fun providesGroupRepository(
        groupRemoteDataSource: GroupRemoteDataSource,
        getGroupPagingSource: GetGroupPagingSource
    ): GroupRepository {
        return GroupRepository(groupRemoteDataSource ,getGroupPagingSource)
    }

}