package dswd.ziacare.app.di

import dswd.ziacare.app.BuildConfig
import dswd.ziacare.app.data.repositories.AppRetrofitService
import dswd.ziacare.app.data.repositories.group.GetGroupPagingSource
import dswd.ziacare.app.data.repositories.group.GroupRemoteDataSource
import dswd.ziacare.app.data.repositories.member.GetAllPendingRequestPagingSource
import dswd.ziacare.app.data.repositories.member.GetListOfMembersPagingSource
import dswd.ziacare.app.data.repositories.member.MemberRemoteDataSource
import dswd.ziacare.app.data.repositories.member.MemberRepository
import dswd.ziacare.app.data.repositories.member.MemberService
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