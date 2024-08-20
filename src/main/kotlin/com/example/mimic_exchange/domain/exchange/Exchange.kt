package com.example.mimic_exchange.domain.exchange

import com.example.mimic_exchange.domain.asset.AssetCode
import java.math.BigDecimal

data class Exchange(
   val id: Int,
   val userId: Int,
   val type: ExchangeType,
   val assetCode: AssetCode,
   val amount: BigDecimal,
   val unitPrice: BigDecimal,
   val totalPrice: BigDecimal,
)
