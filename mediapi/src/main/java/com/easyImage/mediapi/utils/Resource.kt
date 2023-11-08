package com.easyImage.mediapi.utils

import com.google.gson.annotations.SerializedName


class Resource<T> {
    val status: Status

    var data: T? = null

    var message: String? = null


    var errorModel: ServiceErrorModel? = null

    private constructor(status: Status,data: T?,  message: String?) {
        this.status = status
        this.data = data
        this.message = message
    }

    private constructor(status: Status, errorModel: ServiceErrorModel) {
        this.status = status
        this.errorModel = errorModel
    }

    enum class Status {
        SUCCESS, ERROR, LOADING, RESET
    }

    companion object {
        fun <T> success(data: T?): Resource<T?> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> success( data: T?, msg: String): Resource<T?> {
            return Resource(Status.SUCCESS, data, msg)
        }

        fun <T> error(msg: String, data: T?): Resource<T?> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading( data: T): Resource<T?> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> errorModel(errorModel: ServiceErrorModel): Resource<T?> {
            return Resource(Status.ERROR, errorModel)
        }

        fun <T> reset(): Resource<T?> {
            return Resource(Status.RESET, null, null)
        }
    }
}


data class WrapResponse<T>(
    @SerializedName("isSuccess") var success: Boolean? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("error") var error:ErrorResponse? = null,
//@SerializedName("info") var info: String? = null,
    @SerializedName("dateTime") var dateTime: String? = null
)

data class ServiceErrorModel(
    @SerializedName("message") var message: String? = null,
    @SerializedName("code") var code: String? = ""
)

data class ErrorResponse(var code: Int?, var name: String?, var message: String?)


class ErrorConstants private constructor() {
    companion object {
        const val ERROR_MESSAGE_UNKNOWN = "Bilinmeyen bir hata oluştu."
        const val ERROR_CODE_UNKNOWN = "-999"
        const val ERROR_MESSAGE_TIMEOUT = "İnternet bağlantınızı kontrol ediniz."
        const val ERROR_MESSAGE_NO_INTERNET = "İnternet bağlantınızı kontrol ediniz"
        const val ERROR_UNKNOWN_TYPE = "Beklenmeyen veri tipi."
        const val ERROR_EMPTY_DATA = "Sunucudan veri alınamadı."
    }
}