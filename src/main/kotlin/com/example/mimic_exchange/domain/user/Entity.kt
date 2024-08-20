package com.example.mimic_exchange.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user")
data class Entity(
   @Id
   val id: Int,
   val name: String,
)
