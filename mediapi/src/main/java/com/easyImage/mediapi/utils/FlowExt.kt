package com.easyImage.mediapi.utils

import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response


fun <T> Response<WrapResponse<T>>.handleResponse(): Flow<Resource<T?>> =
    flow<Resource<T?>> {
        var response = this@handleResponse

        if (response.isSuccessful) {
            var wrapResponse = response.body()
            if (wrapResponse != null) {
                if (wrapResponse.success == true) {
                    emit(Resource.success(wrapResponse.data, wrapResponse.message.ignoreNull()))
                } else {
                    val errorCode:String = wrapResponse.error?.code?.toString() ?: ""

                    when {
                        isErrorCodeAboutSignContract(errorCode) -> {
                            val dialogDeferred = CompletableDeferred<Resource<T?>>()
                            emit(dialogDeferred.await())
                        }

                        isErrorCodeAboutOTP(errorCode) -> {}

                        else -> {
                            if (wrapResponse.success == true) {
                                emit(Resource.success(wrapResponse.data))
                            } else {
                                val serviceErrorModel = ServiceErrorModel()

                                emit(Resource.errorModel(serviceErrorModel))
                            }
                        }
                    }
                }
            } else {
                emit(Resource.success(wrapResponse?.data))
            }
        } else {
            var serviceErrorModel: ServiceErrorModel? = null

            try {
                val errorBodyString = response.errorBody()?.string()
                val gson = Gson()
                val error = gson.fromJson(errorBodyString, WrapResponse::class.java)?.error
                val message = gson.fromJson(errorBodyString, WrapResponse::class.java)?.message

                serviceErrorModel = ServiceErrorModel()
                serviceErrorModel.code = if (error?.code == null) "-999" else error.code.toString()
                serviceErrorModel.message = error?.message.ignoreNull( message.ignoreNull(ErrorConstants.ERROR_CODE_UNKNOWN))

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (serviceErrorModel == null) {
                serviceErrorModel = ServiceErrorModel(
                    message = ErrorConstants.ERROR_MESSAGE_UNKNOWN,
                    code = ErrorConstants.ERROR_CODE_UNKNOWN
                )
            }

            emit(Resource.errorModel(serviceErrorModel))
        }
    }


private fun isErrorCodeAboutSignContract(errorCode: String): Boolean =
    errorCode == "ErrorType.CONTRACT"

private fun isErrorCodeAboutOTP(errorCode: String): Boolean = errorCode == "ErrorType.OTP"