package com.example.mimic_exchange.domain.transaction

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("transaction")
data class Entity(
   @Id
   val id: Int,
   val userId: Int,
   val type: TransactionType,
   val amount: BigDecimal,
)
