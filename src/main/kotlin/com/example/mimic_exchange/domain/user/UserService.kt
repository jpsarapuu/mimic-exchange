package com.example.mimic_exchange.domain.user

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.example.mimic_exchange.common.big_decimal.trim
import com.example.mimic_exchange.common.error.NotFound
import com.example.mimic_exchange.common.error.NotFoundOrUnprocessable
import com.example.mimic_exchange.common.error.Unprocessable
import com.example.mimic_exchange.domain.conversion.Conversion
import com.example.mimic_exchange.domain.conversion.ConversionForm
import com.example.mimic_exchange.domain.exchange.Exchange
import com.example.mimic_exchange.domain.exchange.ExchangeForm
import com.example.mimic_exchange.domain.exchange.ExchangeService
import com.example.mimic_exchange.domain.exchange.ExchangeType.PURCHASE
import com.example.mimic_exchange.domain.exchange.ExchangeType.SALE
import com.example.mimic_exchange.domain.transaction.Transaction
import com.example.mimic_exchange.domain.transaction.TransactionForm
import com.example.mimic_exchange.domain.transaction.TransactionService
import com.example.mimic_exchange.domain.transaction.TransactionType.DEPOSIT
import com.example.mimic_exchange.domain.transaction.TransactionType.WITHDRAWAL
import com.example.mimic_exchange.domain.user.User.Asset
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation.SERIALIZABLE
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ZERO

@Service
class UserService(
   private val repo: Repo,
   private val transactionService: TransactionService,
   private val exchangeService: ExchangeService,
) {
   @Transactional
   fun createBy(form: UserForm): User {
      val id = 0
      val name = form.name
      val entity = Entity(id, name)
      val created = repo.save(entity)

      return getBy(created.id).getOrElse { throw Exception(it.message) }
   }

   fun getBy(id: Int): Either<NotFound, User> = either {
      val user = repo.findByIdOrNull(id)

      ensureNotNull(user) { NotFound("user not found by ID $id") }

      val name = user.name
      val transactions = transactionService.getAllBy(id)
      val exchanges = exchangeService.getAllBy(id)
      val transactionBalance = transactions.sumOf { it.toBalanceChange() }
      val exchangeBalance = exchanges.sumOf { it.toBalanceChange() }
      val balance = transactionBalance + exchangeBalance
      val assets = exchanges
         .groupBy { it.assetCode }
         .map { (code, assets) ->
            val type = code.type
            val amount = assets.sumOf { it.toAmountChange() }.trim()
            val totalValue = (amount * code.price()).trim()
            Asset(code, type, amount, totalValue)
         }

      User(id, name, transactions, exchanges, balance, assets)
   }

   @Transactional(isolation = SERIALIZABLE)
   fun createTransactionBy(
      id: Int,
      transactionForm: TransactionForm,
   ): Either<NotFoundOrUnprocessable, Transaction> = either {
      val user = getBy(id).bind()
      val (type, amount) = transactionForm
      if (type == WITHDRAWAL) {
         val balance = user.balance
         ensure(balance >= amount) {
            Unprocessable("withdrawal amount ($amount) can not exceed balance ($balance)")
         }
      }

      transactionService.createBy(id, transactionForm).bind()
   }

   @Transactional(isolation = SERIALIZABLE)
   fun createExchangeBy(
      id: Int,
      exchangeForm: ExchangeForm,
   ): Either<NotFoundOrUnprocessable, Exchange> = either {
      val user = getBy(id).bind()
      val (type, assetCode, amount) = exchangeForm
      val totalPrice = amount * assetCode.price()

      when (type) {
         PURCHASE -> {
            val balance = user.balance
            ensure(balance >= totalPrice) {
               Unprocessable("total price ($totalPrice) can not exceed balance ($balance)")
            }
         }

         SALE -> {
            val existingAmount = user.assets.find { it.code == assetCode }?.amount ?: ZERO
            ensure(existingAmount >= amount) {
               Unprocessable("amount ($amount) must not exceed existing amount ($existingAmount)")
            }
         }
      }

      exchangeService.createBy(id, exchangeForm).bind()
   }

   @Transactional(isolation = SERIALIZABLE)
   fun createConversionBy(
      id: Int,
      conversionForm: ConversionForm,
   ) = either {
      val user = getBy(id).bind()
      val (sourceAssetCode, sourceAmount, targetAssetCode) = conversionForm

      ensure(sourceAssetCode != targetAssetCode) {
         Unprocessable("source asset code must differ from target asset code")
      }

      val existingAmount = user.assets.find { it.code == sourceAssetCode }?.amount ?: ZERO

      ensure(existingAmount >= sourceAmount) {
         Unprocessable(
            "source amount ($sourceAmount) must not exceed existing amount ($existingAmount)"
         )
      }

      val saleForm = ExchangeForm(SALE, sourceAssetCode, sourceAmount)
      val sale = exchangeService.createBy(id, saleForm).bind()
      val amount = sale.totalPrice / targetAssetCode.price()
      val purchaseForm = ExchangeForm(PURCHASE, targetAssetCode, amount)
      val purchase = exchangeService.createBy(id, purchaseForm).bind()
      val targetAmount = purchase.amount

      Conversion(sourceAssetCode, sourceAmount, targetAssetCode, targetAmount)
   }
}

private fun Transaction.toBalanceChange() = when (type) {
   DEPOSIT -> amount
   WITHDRAWAL -> -amount
}

private fun Exchange.toBalanceChange() = when (type) {
   PURCHASE -> -totalPrice
   SALE -> totalPrice
}

private fun Exchange.toAmountChange() = when (type) {
   PURCHASE -> amount
   SALE -> -amount
}
