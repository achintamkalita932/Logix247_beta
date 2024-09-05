package com.demo.logix247_beta.Models

data class Orders(
    var id : String = "",
    val createdBy : String = "",
    val assignedTo : String = "",
    val itemOwner: String = "",
    val deliveryPartner: String = "",
    val orderStatus : String = "pending",
    val itemDescription : String = "",
    val quantity : String = "",
    val weightAndSize : String = "",
    val valueGoods : String = "",
    val packagingType : String = "",
    val fragileGoods : String = "",
    val paymentModeForDelivery : String = "",
    val shippingAddressForDelivery : String = "",
    val pickupAddressForDelivery : String = "",
    val shippedDate : String = "",
)
