package com.lionscare.app.di

import com.lionscare.app.BuildConfig
import com.lionscare.app.data.repositories.article.ArticleRemoteDataSource
import com.lionscare.app.data.repositories.article.ArticleRepository
import com.lionscare.app.data.repositories.article.ArticleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ArticleModule {

    @Provides
    fun providesArticleService(): ArticleService {
        return com.lionscare.app.data.repositories.AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            ArticleService::class.java
        )
    }

    @Provides
    fun providesArticleRemoteDataSource(authService: ArticleService): ArticleRemoteDataSource {
        return ArticleRemoteDataSource(authService)
    }

    @Provides
    fun providesArticleRepository(authRemoteDataSource: ArticleRemoteDataSource): ArticleRepository {
        return ArticleRepository(authRemoteDataSource)
    }

}