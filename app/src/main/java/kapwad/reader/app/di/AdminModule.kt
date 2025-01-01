package kapwad.reader.app.di

import kapwad.reader.app.BuildConfig
import kapwad.reader.app.data.repositories.AppRetrofitService
import kapwad.reader.app.data.repositories.admin.AdminRemoteDataSource
import kapwad.reader.app.data.repositories.admin.AdminRepository
import kapwad.reader.app.data.repositories.admin.AdminService
import kapwad.reader.app.data.repositories.admin.GetListOfAdminPagingSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AdminModule {

    @Provides
    fun providesAdminService(): AdminService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AdminService::class.java
        )
    }

    @Provides
    fun providesAdminRemoteDataSource(adminService: AdminService): AdminRemoteDataSource {
        return AdminRemoteDataSource(adminService)
    }

    @Provides
    fun providesGetListOfAdminPagingSource(adminRemoteDataSource: AdminRemoteDataSource): GetListOfAdminPagingSource {
        return GetListOfAdminPagingSource(adminRemoteDataSource)
    }

    @Provides
    fun providesAdminRepository(
        adminRemoteDataSource: AdminRemoteDataSource
    ): AdminRepository {
        return AdminRepository(adminRemoteDataSource)
    }

    
}