package com.lionscare.app.ui.article.viewmodel

import com.lionscare.app.data.repositories.article.response.ArticleListResponse
import com.lionscare.app.utils.PopupErrorState

sealed class ArticleListViewState{
    object Loading : ArticleListViewState()
    data class Success(val articleListResponse: ArticleListResponse? = ArticleListResponse()) : ArticleListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleListViewState()
}
