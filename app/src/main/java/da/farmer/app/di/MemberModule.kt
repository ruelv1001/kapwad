package da.farmer.app.di

import da.farmer.app.BuildConfig
import da.farmer.app.data.repositories.AppRetrofitService
import da.farmer.app.data.repositories.group.GetGroupPagingSource
import da.farmer.app.data.repositories.group.GroupRemoteDataSource
import da.farmer.app.data.repositories.member.GetAllPendingRequestPagingSource
import da.farmer.app.data.repositories.member.GetListOfMembersPagingSource
import da.farmer.app.data.repositories.member.MemberRemoteDataSource
import da.farmer.app.data.repositories.member.MemberRepository
import da.farmer.app.data.repositories.member.MemberService
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