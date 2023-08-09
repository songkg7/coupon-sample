package com.example.couponsample.coupon.service

import com.example.couponsample.coupon.entity.Coupon
import com.example.couponsample.coupon.repository.CouponRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private lateinit var couponService: CouponService

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @Autowired
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setUp() {
        val coupon = Coupon(name = "Toss Slash", availableStock = 100)
        couponRepository.save(coupon)
    }

    @Test
    @DisplayName("쿠폰 차감 분산락 미적용 100 명 테스트")
    fun decreaseStock() {
        val numberOfThreads = 100
        val executorService: ExecutorService = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        for (i in 1..numberOfThreads) {
            executorService.submit {
                couponService.decreaseStock(1L)
                latch.countDown()
            }
        }

        latch.await()

        val coupon = couponRepository.findById(1L).orElseThrow()
        println("잔여 쿠폰 개수 = ${coupon.getAvailableStock()}")
        assertThat(coupon.getAvailableStock()).isGreaterThan(0)
    }

    @Test
    @DisplayName("쿠폰 차감 분산락 적용 100 명 테스트")
    fun decreaseStockWithLock() {
        val numberOfThreads = 100
        val executorService: ExecutorService = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        for (i in 1..numberOfThreads) {
            executorService.submit {
                couponService.decreaseStockWithLock(1L)
                latch.countDown()
            }
        }

        latch.await()

        val coupon = couponRepository.findById(1L).orElseThrow()
        println("잔여 쿠폰 개수 = ${coupon.getAvailableStock()}")
        assertThat(coupon.getAvailableStock()).isEqualTo(0)
    }

}
