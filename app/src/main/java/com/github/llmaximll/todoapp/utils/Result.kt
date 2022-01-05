package com.github.llmaximll.todoapp.utils

sealed class Result<out S, out E> {
    data class Success<out S>(val result: S) : Result<S, Nothing>()
    data class Error<out E>(val result: E) : Result<Nothing, E>()
}

inline fun <S, E, R> Result<S, E>.mapSuccess(block: (S) -> R): Result<R, E> =
    when (this) {
        is Result.Success -> Result.Success(result = block(this.result))
        is Result.Error -> Result.Error(result = this.result)
    }

inline fun <S, E, R> Result<S, E>.mapError(block: (E) -> R): Result<S, R> =
    when (this) {
        is Result.Success -> Result.Success(result = this.result)
        is Result.Error -> Result.Error(result = block(this.result))
    }