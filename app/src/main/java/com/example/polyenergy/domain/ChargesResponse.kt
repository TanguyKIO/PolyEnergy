package com.example.polyenergy.domain

class ChargesResponse(infos: List<ChargeInfo>?) {

    private var charges: List<ChargeInfo>
    private var isSuccessful: Boolean = false

    init {
        isSuccessful = infos != null
        if (infos != null) {
            charges = infos
        } else {
            charges = mutableListOf()
        }
    }

    fun getAddresses(): List<ChargeInfo> {
        return charges
    }

    fun isSuccessful(): Boolean {
        return isSuccessful
    }
}