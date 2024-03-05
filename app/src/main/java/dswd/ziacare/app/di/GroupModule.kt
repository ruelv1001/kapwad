package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.group.GetGroupPagingSource
import dswd.ziacare.app.data.repositories.group.GroupRemoteDataSource
import dswd.ziacare.app.data.repositories.group.GroupRepository
import dswd.ziacare.app.data.repositories.group.GroupService
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