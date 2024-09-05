package com.demo.logix247_beta.Models

data class Partner(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val isPartner: Boolean = false,
    val partnerDetails: PartnerDetails = PartnerDetails()
)

data class PartnerDetails(
    val companyAddress: String = "",
    val companyEmail: String = "",
    val companyName: String = "",
    val contactName: String = "",
    val contactNumber: String = "",
    val range: String = "",
    val rating: String = "",
    val serviceArea: List<String> = listOf()
)

