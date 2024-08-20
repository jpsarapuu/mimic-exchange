package com.example.mimic_exchange

import com.example.mimic_exchange.domain.asset.AssetCode.BTC
import com.example.mimic_exchange.domain.asset.AssetCode.SILVER
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.openapitools.client.apis.UserApi
import org.openapitools.client.models.*
import org.openapitools.client.models.ConversionForm.SourceAssetCode
import org.openapitools.client.models.ConversionForm.TargetAssetCode
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

private val USER_API = UserApi(baseUrl = "http://localhost:8080")
private val USER_FORM = UserForm(name = "John Doe")

class ApiSpec : FreeSpec() {
   init {
      "creates a new user" {
         val user = USER_API.createUserBy(USER_FORM)

         user should {
            it.id shouldBeGreaterThan 0
            it.name shouldBe USER_FORM.name
            it.transactions shouldHaveSize 0
            it.exchanges shouldHaveSize 0
            it.balance shouldBe ZERO
            it.assets shouldHaveSize 0
         }
      }

      "creates a deposit" - {
         val user = USER_API.createUserBy(USER_FORM)
         val amount = 1000.toBigDecimal()
         val transactionForm = deposit(amount)
         val transaction = USER_API.createTransactionBy(user.id, transactionForm)

         "with new transaction" {
            transaction should {
               it.id shouldBeGreaterThan 0
               it.userId shouldBe user.id
               it.type shouldBe Transaction.Type.DEPOSIT
               it.amount shouldBe amount
            }
         }

         "and increases user balance by deposited amount" {
            val updatedUser = USER_API.getUserBy(user.id)

            updatedUser should {
               it.transactions shouldBe listOf(transaction)
               it.balance shouldBe amount
            }
         }
      }

      "tries to create a withdrawal transaction with insufficient funds" {
         val user = USER_API.createUserBy(USER_FORM)
         val depositAmount = 1000.toBigDecimal()
         val depositForm = deposit(depositAmount)
         val withdrawalAmount = 1500.toBigDecimal()
         val withdrawalForm = withdrawal(withdrawalAmount)

         USER_API.createTransactionBy(user.id, depositForm)

         shouldThrowMessage(
            message = """
            422 Unprocessable Entity: "{"message":"withdrawal amount (1500) can not exceed balance (1000)"}"
         """.trimIndent()
         ) {
            USER_API.createTransactionBy(user.id, withdrawalForm)
         }
      }

      "creates withdrawal" - {
         val user = USER_API.createUserBy(USER_FORM)
         val depositAmount = 1000.toBigDecimal()
         val depositForm = deposit(depositAmount)
         val withdrawalAmount = 750.toBigDecimal()
         val withdrawalForm = withdrawal(withdrawalAmount)
         val depositTransaction = USER_API.createTransactionBy(user.id, depositForm)
         val withdrawalTransaction = USER_API.createTransactionBy(user.id, withdrawalForm)

         "with new transaction" {
            withdrawalTransaction should {
               it.id shouldBeGreaterThan 0
               it.userId shouldBe user.id
               it.type shouldBe Transaction.Type.WITHDRAWAL
               it.amount shouldBe withdrawalAmount
            }
         }

         "and decreases user balance by withdrawn amount" {
            val updatedUser = USER_API.getUserBy(user.id)

            updatedUser should {
               it.transactions shouldBe listOf(depositTransaction, withdrawalTransaction)
               it.balance shouldBe 250.toBigDecimal()
            }
         }
      }

      "creates a purchase" - {
         val user = userWithBalance(10_000)
         val btcAmount = 0.75.toBigDecimal()
         val exchangeForm = btcPurchase(btcAmount)
         val exchange = USER_API.createExchangeBy(user.id, exchangeForm)

         "with new exchange" {
            exchange should {
               it.id shouldBeGreaterThan 0
               it.userId shouldBe user.id
               it.type shouldBe Exchange.Type.PURCHASE
               it.assetCode shouldBe Exchange.AssetCode.BTC
               it.amount shouldBe btcAmount
               it.unitPrice shouldBe BTC.price()
               it.totalPrice shouldBe 7500.toBigDecimal()
            }
         }

         "and updates user's balance and assets accordingly" {
            val updatedUser = USER_API.getUserBy(user.id)

            updatedUser should {
               it.exchanges shouldBe listOf(exchange)
               it.balance shouldBe 2500.toBigDecimal()
               it.assets shouldBe listOf(
                  Asset(
                     code = Asset.Code.BTC,
                     type = Asset.Type.CRYPTO,
                     amount = btcAmount,
                     totalValue = 7500.toBigDecimal()
                  )
               )
            }
         }
      }

      "creates a sale" - {
         val user = userWithBalance(6000)
         val purchaseAmount = 200.toBigDecimal()
         val purchaseForm = silverPurchase(purchaseAmount)
         val saleAmount = 150.toBigDecimal()
         val saleForm = silverSale(saleAmount)
         val purchase = USER_API.createExchangeBy(user.id, purchaseForm)
         val sale = USER_API.createExchangeBy(user.id, saleForm)

         "with new exchange" {
            sale should {
               it.id shouldBeGreaterThan 0
               it.userId shouldBe user.id
               it.type shouldBe Exchange.Type.SALE
               it.assetCode shouldBe Exchange.AssetCode.SILVER
               it.amount shouldBe saleAmount
               it.unitPrice shouldBe SILVER.price()
               it.totalPrice shouldBe 3750.toBigDecimal()
            }
         }

         "and updates user's balance and assets accordingly" {
            val updatedUser = USER_API.getUserBy(user.id)

            updatedUser should {
               it.exchanges shouldBe listOf(purchase, sale)
               it.balance shouldBe 4750.toBigDecimal()
               it.assets shouldBe listOf(
                  Asset(
                     code = Asset.Code.SILVER,
                     type = Asset.Type.COMMODITY,
                     amount = 50.toBigDecimal(),
                     totalValue = 1250.toBigDecimal()
                  )
               )
            }
         }
      }

      "creates a conversion" - {
         val user = userWithBalance(12_000)
         val purchaseAmount = 300.toBigDecimal()
         val purchaseForm = silverPurchase(purchaseAmount)
         USER_API.createExchangeBy(user.id, purchaseForm)

         val sourceAssetCode = SourceAssetCode.SILVER
         val sourceAmount = 200.toBigDecimal()
         val targetAssetCode = TargetAssetCode.TESLA
         val conversionForm = ConversionForm(sourceAssetCode, sourceAmount, targetAssetCode)
         val conversion = USER_API.createConversionBy(user.id, conversionForm)

         "with expected result" {
            conversion shouldBe Conversion(
               sourceAssetCode = Conversion.SourceAssetCode.SILVER,
               sourceAmount = sourceAmount,
               targetAssetCode = Conversion.TargetAssetCode.TESLA,
               targetAmount = 25.toBigDecimal()
            )
         }

         "and updates user's balance and assets accordingly" {
            val updatedUser = USER_API.getUserBy(user.id)

            updatedUser should {
               it.balance shouldBe 4500.toBigDecimal()
               it.assets shouldBe listOf(
                  Asset(
                     code = Asset.Code.SILVER,
                     type = Asset.Type.COMMODITY,
                     amount = 100.toBigDecimal(),
                     totalValue = 2500.toBigDecimal()
                  ),
                  Asset(
                     code = Asset.Code.TESLA,
                     type = Asset.Type.STOCK,
                     amount = 25.toBigDecimal(),
                     totalValue = 5000.toBigDecimal()
                  )
               )
            }
         }
      }
   }
}

private fun userWithBalance(amount: Int): User {
   val user = USER_API.createUserBy(USER_FORM)
   val transactionForm = deposit(amount.toBigDecimal())
   USER_API.createTransactionBy(user.id, transactionForm)

   return user
}

private fun deposit(amount: BigDecimal) = TransactionForm(
   type = TransactionForm.Type.DEPOSIT,
   amount = amount
)

private fun withdrawal(amount: BigDecimal) = TransactionForm(
   type = TransactionForm.Type.WITHDRAWAL,
   amount = amount
)

private fun btcPurchase(amount: BigDecimal) = ExchangeForm(
   type = ExchangeForm.Type.PURCHASE,
   assetCode = ExchangeForm.AssetCode.BTC,
   amount = amount
)

private fun silverPurchase(amount: BigDecimal) = ExchangeForm(
   type = ExchangeForm.Type.PURCHASE,
   assetCode = ExchangeForm.AssetCode.SILVER,
   amount = amount
)

private fun silverSale(amount: BigDecimal) = ExchangeForm(
   type = ExchangeForm.Type.SALE,
   assetCode = ExchangeForm.AssetCode.SILVER,
   amount = amount
)
