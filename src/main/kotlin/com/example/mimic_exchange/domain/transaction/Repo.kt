package com.example.mimic_exchange.domain.transaction

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository("TransactionRepo")
interface Repo : CrudRepository<Entity, Int> {

   fun getAllByUserId(userId: Int): List<Entity>
}
