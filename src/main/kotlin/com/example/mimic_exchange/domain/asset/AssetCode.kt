package com.example.mimic_exchange.domain.asset

import com.example.mimic_exchange.domain.asset.AssetType.*
import java.math.BigDecimal

enum class AssetCode(
   val type: AssetType,
   private val price: Int,
) {
   BTC(CRYPTO, 10_000),
   ETH(CRYPTO, 1_000),
   APPLE(STOCK, 100),
   TESLA(STOCK, 200),
   GOLD(COMMODITY, 50),
   SILVER(COMMODITY, 25);

   fun price(): BigDecimal = price.toBigDecimal()
}
