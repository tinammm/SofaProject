package com.sofascoremini.util

import retrofit2.HttpException
import retrofit2.Response
import com.sofascoremini.data.remote.Result

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