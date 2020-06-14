package com.example.resdelivery.util

import com.example.resdelivery.util.Result.*


sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error<out T>(val data: T) : Result<T>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$data loading from db]"
            Loading -> "Loading"
        }
    }
}

inline fun <T : Any> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Success) action(data)
    return this
}

inline fun <T : Any> Result<T>.onError(action: (T) -> Unit): Result<T> {
    if (this is Error) action(data)
    return this
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Success && data != null
