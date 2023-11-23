package com.ziacare.app.di

import com.ziacare.app.BuildConfig
import com.ziacare.app.data.repositories.AppRetrofitService
import com.ziacare.app.data.repositories.group.GetGroupPagingSource
import com.ziacare.app.data.repositories.group.GroupRemoteDataSource
import com.ziacare.app.data.repositories.member.GetAllPendingRequestPagingSource
import com.ziacare.app.data.repositories.member.GetListOfMembersPagingSource
import com.ziacare.app.data.repositories.member.MemberRemoteDataSource
import com.ziacare.app.data.repositories.member.MemberRepository
import com.ziacare.app.data.repositories.member.MemberService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class MemberModule {

    @Provides
    fun providesMemberService(): MemberService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            MemberService::class.java
        )
    }

    @Provides
    fun providesMemberRemoteDataSource(memberService: MemberService): MemberRemoteDataSource {
        return MemberRemoteDataSource(memberService)
    }

    @Provides
    fun providesMemberRepository(
        memberRemoteDataSource: MemberRemoteDataSource
    ): MemberRepository {
        return MemberRepository(memberRemoteDataSource)
    }

    
}