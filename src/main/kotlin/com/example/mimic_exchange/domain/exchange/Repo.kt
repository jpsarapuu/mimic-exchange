package com.example.mimic_exchange.domain.exchange

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository("ExchangeRepo")
interface Repo : CrudRepository<Entity, Int> {

   fun getAllByUserId(userId: Int): List<Entity>
}
