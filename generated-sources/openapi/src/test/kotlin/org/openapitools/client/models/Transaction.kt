/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
   "ArrayInDataClass",
   "EnumEntryName",
   "RemoveRedundantQualifierName",
   "UnusedImport"
)

package org.openapitools.client.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 *
 * @param id
 * @param userId
 * @param type
 * @param amount
 */


data class Transaction(

   @field:JsonProperty("id")
   val id: kotlin.Int,

   @field:JsonProperty("userId")
   val userId: kotlin.Int,

   @field:JsonProperty("type")
   val type: TransactionType,

   @field:JsonProperty("amount")
   val amount: java.math.BigDecimal,

   ) {


}

