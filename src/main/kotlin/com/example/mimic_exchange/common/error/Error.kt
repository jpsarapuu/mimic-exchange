package com.example.mimic_exchange.common.error

import arrow.core.Either
import arrow.core.getOrElse
import com.example.mimic_exchange.common.web_mvc.NotFoundException
import com.example.mimic_exchange.common.web_mvc.UnprocessableException

sealed interface NotFoundOrUnprocessable

data class NotFound(val message: String) : NotFoundOrUnprocessable
data class Unprocessable(val message: String) : NotFoundOrUnprocessable

fun <T> Either<NotFoundOrUnprocessable, T>.throwLeft() = getOrElse { error ->
   when (error) {
      is NotFound -> throw NotFoundException(error.message)
      is Unprocessable -> throw UnprocessableException(error.message)
   }
}
