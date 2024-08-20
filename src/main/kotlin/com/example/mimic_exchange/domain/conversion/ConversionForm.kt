package com.example.mimic_exchange.domain.conversion

import com.example.mimic_exchange.domain.asset.AssetCode
import java.math.BigDecimal

data class ConversionForm(
   val sourceAssetCode: AssetCode,
   val sourceAmount: BigDecimal,
   val targetAssetCode: AssetCode,
)
