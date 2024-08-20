package com.example.mimic_exchange.domain.transaction

import java.math.BigDecimal

data class Transaction(
   val id: Int,
   val userId: Int,
   val type: TransactionType,
   val amount: BigDecimal,
)
