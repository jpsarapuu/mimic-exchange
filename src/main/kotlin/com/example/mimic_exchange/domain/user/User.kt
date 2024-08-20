package com.example.mimic_exchange.domain.user

import com.example.mimic_exchange.domain.asset.AssetCode
import com.example.mimic_exchange.domain.asset.AssetType
import com.example.mimic_exchange.domain.exchange.Exchange
import com.example.mimic_exchange.domain.transaction.Transaction
import java.math.BigDecimal

data class User(
   val id: Int,
   val name: String,
   val transactions: List<Transaction>,
   val exchanges: List<Exchange>,
   val balance: BigDecimal,
   val assets: List<Asset>,
) {
   data class Asset(
      val code: AssetCode,
      val type: AssetType,
      val amount: BigDecimal,
      val totalValue: BigDecimal,
   )
}
