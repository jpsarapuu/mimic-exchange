package com.example.mimic_exchange.domain.transaction

import java.math.BigDecimal

data class TransactionForm(
   val type: TransactionType,
   val amount: BigDecimal,
)
