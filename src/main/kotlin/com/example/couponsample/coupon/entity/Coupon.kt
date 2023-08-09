package com.example.couponsample.coupon.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Coupon(
    @Id
    @GeneratedValue
    private val id: Long? = null,

    private val name: String,

    private var availableStock: Int,
) {

    fun decrease() {
        validateStockCount()
        availableStock--
    }

    private fun validateStockCount() {
        check(availableStock > 0) { "재고가 없습니다." }
    }

    fun getAvailableStock(): Int {
        return availableStock
    }

}
