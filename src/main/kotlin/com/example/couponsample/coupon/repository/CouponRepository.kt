package com.example.couponsample.coupon.repository

import com.example.couponsample.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponRepository : JpaRepository<Coupon, Long> {
}
