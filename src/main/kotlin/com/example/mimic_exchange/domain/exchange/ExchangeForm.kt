package com.example.mimic_exchange.domain.exchange

import com.example.mimic_exchange.domain.asset.AssetCode
import java.math.BigDecimal

data class ExchangeForm(
   val type: ExchangeType,
   val assetCode: AssetCode,
   val amount: BigDecimal,
)
