package com.sofascoremini.util

import com.sofascoremini.data.remote.Result
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeResponse(func: suspend () -> T): Result<T> {
    return try {
        val result = func.invoke()
        if (result is Response<*> && result.isSuccessful.not()) {
            Result.Error(HttpException(result))
        } else {
            Result.Success(result)
        }
    } catch (e: Throwable) {
        Result.Error(e)
    }
}