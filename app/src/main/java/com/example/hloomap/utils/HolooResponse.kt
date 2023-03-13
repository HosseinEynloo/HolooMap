package com.example.hloomap.utils



class HolooResponse<out T>(val status: Status, val data: T? = null, val message: String? = null) {


    enum class Status { LOADING, SUCCESS, ERROR }

    companion object {
        fun <T> loading(): HolooResponse<T> {
            return HolooResponse(Status.LOADING)
        }

        fun <T> success(data: T?): HolooResponse<T> {
            return HolooResponse(Status.SUCCESS, data)
        }

        fun <T> error(error: String): HolooResponse<T> {
            return HolooResponse(Status.ERROR, message = error)
        }
    }
}