package com.example.smartpull_upbar.domain

sealed class Resource <out T : Any> {

    data class Success <out T : Any>
    (
      val data: T,
      val topSuccessMessage: String,
      val bottomSuccessMessage: String? = null
    ) : Resource<T>()

    data class Error
    (
       val errorMessage : String
    )
    : Resource <Nothing> ()

    data class Loading <out T:Any>
    (
      val data : T? = null,
      val topLoadingMessage : String,
      val bottomLoadingMessage : String? = null
    )
    : Resource<T>()
}