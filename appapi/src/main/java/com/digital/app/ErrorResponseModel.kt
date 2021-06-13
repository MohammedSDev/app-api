package com.digital.app


interface ErrorResponseModel : ResponseModel {
  var message: String
  var throwable: Throwable?
}