package com.example.mimic_exchange.domain.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository("UserRepo")
interface Repo : CrudRepository<Entity, Int>
