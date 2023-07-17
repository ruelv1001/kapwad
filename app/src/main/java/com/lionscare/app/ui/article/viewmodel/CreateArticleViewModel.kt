package com.lionscare.app.ui.article.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lionscare.app.data.repositories.article.ArticleRepository
import com.lionscare.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class CreateArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _articleSharedFlow = MutableSharedFlow<CreateArticleViewState>()

    val articleSharedFlow: SharedFlow<CreateArticleViewState> =
        _articleSharedFlow.asSharedFlow()

    lateinit var imageFile: File


    fun doCreateArticle(name: String, desc: String, imageFile: File) {
        viewModelScope.launch {
            articleRepository.doCreateArticle(name, desc, imageFile)
                .onStart {
                    _articleSharedFlow.emit(CreateArticleViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _articleSharedFlow.emit(
                        CreateArticleViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _articleSharedFlow.emit(
                    CreateArticleViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<com.lionscare.app.data.model.ErrorModel>() {}.type
                var errorResponse: com.lionscare.app.data.model.ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _articleSharedFlow.emit(CreateArticleViewState.InputError(errorResponse.errors))
                } else {
                    _articleSharedFlow.emit(
                        CreateArticleViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }
            }
            else -> _articleSharedFlow.emit(
                CreateArticleViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}