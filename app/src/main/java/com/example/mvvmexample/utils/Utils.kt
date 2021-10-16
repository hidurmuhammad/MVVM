package com.example.mvvmexample.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Response

object Utils {
    fun <T : Any> handleApiError(resp: Response<T>): AppResult.Error {
        val error = ApiErrorUtils.parseError(resp)
        try {
            Log.d("error for url",error.toString()+ resp.raw().request.url.toString())
            throw CustomApiException(resp.code().toString(), resp.raw().request.url.toString())
        } catch (e: CustomApiException) {
            FirebaseCrashlytics.getInstance().log(Log.DEBUG.toString()+ "Error Code : " + resp.code().toString()+ "Url : " + resp.raw().request.url.toString())
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return AppResult.Error(Exception(error.message))
    }

    fun <T : Any> handleSuccess(response: Response<T>): AppResult<T> {
        response.body()?.let {
            return AppResult.Success(it)
        } ?: return handleApiError(response)
    }
}