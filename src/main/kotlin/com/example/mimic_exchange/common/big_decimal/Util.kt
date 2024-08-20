package com.example.mimic_exchange.common.big_decimal

import java.math.BigDecimal

fun BigDecimal.trim(): BigDecimal {
   val stripped = stripTrailingZeros()
   return if (stripped.scale() < 0) stripped.setScale(0) else stripped
}