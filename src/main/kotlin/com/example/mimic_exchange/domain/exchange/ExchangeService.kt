package com.example.mimic_exchange.domain.exchange

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.mimic_exchange.common.big_decimal.trim
import com.example.mimic_exchange.common.error.Unprocessable
import org.springframework.stereotype.Service
import java.math.BigDecimal.ZERO

@Service
class ExchangeService(
   private val repo: Repo,
) {
   fun createBy(userId: Int, form: ExchangeForm): Either<Unprocessable, Exchange> = either {
      val id = 0
      val (type, asset, amount) = form

      ensure(amount > ZERO) { Unprocessable("exchange amount must be positive") }

      val unitPrice = asset.price()
      val totalPrice = amount * unitPrice
      val entity = Entity(id, userId, type, asset, amount, unitPrice, totalPrice)
      val created = repo.save(entity)

      created.toExchange()
   }

   fun getAllBy(userId: Int): List<Exchange> {
      val entities = repo.getAllByUserId(userId)
      return entities.map { it.toExchange() }
   }
}

private fun Entity.toExchange() =
   Exchange(
      id = id,
      userId = userId,
      type = type,
      assetCode = assetCode,
      amount = amount.trim(),
      unitPrice = unitPrice.trim(),
      totalPrice = totalPrice.trim()
   )