package com.example.mimic_exchange.domain.user

import com.example.mimic_exchange.common.error.throwLeft
import com.example.mimic_exchange.domain.conversion.Conversion
import com.example.mimic_exchange.domain.conversion.ConversionForm
import com.example.mimic_exchange.domain.exchange.Exchange
import com.example.mimic_exchange.domain.exchange.ExchangeForm
import com.example.mimic_exchange.domain.transaction.Transaction
import com.example.mimic_exchange.domain.transaction.TransactionForm
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "User")
@RestController
@RequestMapping("/users")
class Controller(
   private val userService: UserService,
) {
   @PostMapping
   fun createUserBy(@RequestBody form: UserForm): User = userService.createBy(form)

   @GetMapping("/{userId}")
   fun getUserBy(@PathVariable userId: Int): User = userService.getBy(userId).throwLeft()

   @PostMapping("/{userId}/transactions")
   fun createTransactionBy(
      @PathVariable userId: Int,
      @RequestBody transactionForm: TransactionForm,
   ): Transaction = userService.createTransactionBy(userId, transactionForm).throwLeft()

   @PostMapping("/{userId}/exchanges")
   fun createExchangeBy(
      @PathVariable userId: Int,
      @RequestBody exchangeForm: ExchangeForm,
   ): Exchange = userService.createExchangeBy(userId, exchangeForm).throwLeft()

   @PostMapping("/{userId}/conversions")
   fun createConversionBy(
      @PathVariable userId: Int,
      @RequestBody conversionForm: ConversionForm,
   ): Conversion = userService.createConversionBy(userId, conversionForm).throwLeft()
}