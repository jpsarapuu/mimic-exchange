package com.example.mimic_exchange.domain.transaction

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.mimic_exchange.common.big_decimal.trim
import com.example.mimic_exchange.common.error.Unprocessable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation.SERIALIZABLE
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ZERO

@Service
class TransactionService(
   private val repo: Repo,
) {
   @Transactional(isolation = SERIALIZABLE)
   fun createBy(userId: Int, form: TransactionForm): Either<Unprocessable, Transaction> = either {
      val id = 0
      val (type, amount) = form

      ensure(amount > ZERO) { Unprocessable("transaction amount must be positive") }
      ensure(amount.scale() <= 2) { Unprocessable("transaction amount's scale most not exceed 2") }

      val entity = Entity(id, userId, type, amount)
      val created = repo.save(entity)
      created.toTransaction()
   }

   fun getAllBy(userId: Int): List<Transaction> {
      val entities = repo.getAllByUserId(userId)
      return entities.map { it.toTransaction() }
   }
}

private fun Entity.toTransaction() = Transaction(
   id = id,
   userId = userId,
   type = type,
   amount = amount.trim()
)