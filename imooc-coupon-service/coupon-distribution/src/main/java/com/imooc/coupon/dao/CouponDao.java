package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义</h1>
 */
public interface CouponDao extends JpaRepository<Coupon, Integer> {
    List<Coupon> findAllByIdAndStatus(Long userId, CouponStatus status);
}
