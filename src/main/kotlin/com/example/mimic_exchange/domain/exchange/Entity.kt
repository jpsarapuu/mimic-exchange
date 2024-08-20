package com.example.mimic_exchange.domain.exchange

import com.example.mimic_exchange.domain.asset.AssetCode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("exchange")
data class Entity(
   @Id
   val id: Int,
   val userId: Int,
   val type: ExchangeType,
   val assetCode: AssetCode,
   val amount: BigDecimal,
   val unitPrice: BigDecimal,
   val totalPrice: BigDecimal,
)
