package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.AppRetrofitService
import com.lionscare.app.data.repositories.member.MemberRemoteDataSource
import com.lionscare.app.data.repositories.member.MemberRepository
import com.lionscare.app.data.repositories.member.MemberService
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