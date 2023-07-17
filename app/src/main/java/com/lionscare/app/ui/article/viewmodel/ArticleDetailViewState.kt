package com.lionscare.app.ui.article.viewmodel

import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.utils.PopupErrorState

sealed class ArticleDetailViewState{
    object Loading : ArticleDetailViewState()
    data class Success(val articleData: ArticleData) : ArticleDetailViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleDetailViewState()
}
