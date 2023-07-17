package com.lionscare.app.ui.article.viewmodel

import com.lionscare.app.utils.PopupErrorState

sealed class CreateArticleViewState{
    object Loading : CreateArticleViewState()
    data class Success(val message: String = "") : CreateArticleViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : CreateArticleViewState()
    data class InputError(val errorData: com.lionscare.app.data.model.ErrorsData? = null) : CreateArticleViewState()
}
