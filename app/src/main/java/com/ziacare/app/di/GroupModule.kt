package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.group.GetGroupPagingSource
import com.ziacare.app.data.repositories.group.GroupRemoteDataSource
import com.ziacare.app.data.repositories.group.GroupRepository
import com.ziacare.app.data.repositories.group.GroupService
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