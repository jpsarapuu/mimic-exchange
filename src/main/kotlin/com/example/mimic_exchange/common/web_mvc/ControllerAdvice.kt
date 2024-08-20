package com.example.mimic_exchange.common.web_mvc

import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

   @ExceptionHandler
   fun handle(exception: Exception): ResponseEntity<WebMvcError> {
      val body = WebMvcError(exception.message)
      val status = when (exception) {
         is NotFoundException -> NOT_FOUND
         is UnprocessableException -> UNPROCESSABLE_ENTITY
         else -> INTERNAL_SERVER_ERROR
      }

      return ResponseEntity.status(status).body(body)
   }
}

data class WebMvcError(
   val message: String?,
)

class NotFoundException(message: String) : Exception(message)
class UnprocessableException(message: String) : Exception(message)