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
 * @param code
 * @param type
 * @param amount
 * @param totalValue
 */


data class Asset(

   @field:JsonProperty("code")
   val code: AssetCode,

   @field:JsonProperty("type")
   val type: AssetType,

   @field:JsonProperty("amount")
   val amount: java.math.BigDecimal,

   @field:JsonProperty("totalValue")
   val totalValue: java.math.BigDecimal,

   ) {


}

