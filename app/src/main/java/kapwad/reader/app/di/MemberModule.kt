package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.member.MemberRemoteDataSource
import kapwad.reader.app.data.repositories.member.MemberRepository
import kapwad.reader.app.data.repositories.member.MemberService
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