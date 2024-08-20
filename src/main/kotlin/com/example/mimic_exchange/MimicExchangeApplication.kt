package com.example.mimic_exchange

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
   info = Info(title = "Mimic Exchange", version = "1"),
   servers = [Server(url = "/", description = "Default server URL")]
)
@SpringBootApplication
class MimicExchangeApplication

fun main(args: Array<String>) {
   runApplication<MimicExchangeApplication>(*args)
}
