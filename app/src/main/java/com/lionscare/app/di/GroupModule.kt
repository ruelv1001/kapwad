package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.group.GetGroupPagingSource
import com.lionscare.app.data.repositories.group.GroupRemoteDataSource
import com.lionscare.app.data.repositories.group.GroupRepository
import com.lionscare.app.data.repositories.group.GroupService
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
    fun providesGroupRepository(
        groupRemoteDataSource: GroupRemoteDataSource,
        getGroupPagingSource: GetGroupPagingSource
    ): GroupRepository {
        return GroupRepository(groupRemoteDataSource ,getGroupPagingSource)
    }

}