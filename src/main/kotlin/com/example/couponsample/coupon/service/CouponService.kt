package com.example.couponsample.coupon.service

import com.example.couponsample.coupon.repository.CouponRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit.SECONDS

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val redissonClient: RedissonClient,
) {

    @Transactional
    fun decreaseStock(id: Long) {
        val coupon = couponRepository.findById(id).orElseThrow()
        coupon.decrease()
    }

    fun decreaseStockWithLock(id: Long) {
        val lock = redissonClient.getLock("coupon-${id}")
        try {
            val available = lock.tryLock(5L, 3L, SECONDS)
            if (!available) {
                throw RuntimeException("lock not available")
            }
            val coupon = couponRepository.findById(id).orElseThrow()
            coupon.decrease()
            // transactional 이 없으므로 save 명시적 호출
            couponRepository.save(coupon)
        } catch (e: InterruptedException) {
            throw e
        } finally {
            lock.unlock()
        }

    }

}
