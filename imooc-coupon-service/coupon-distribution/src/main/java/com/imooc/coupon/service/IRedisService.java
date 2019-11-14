package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * <h1>Ridis 相关的操作服务</h1>
 */
public interface IRedisService {
    /**
     * <h2>根据userId 和状态 找到缓存的优惠券列表数据</h2>
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> getCachedCoupon(Long userId, Integer status);

    /**
     * <h2>保存空的优惠券列表到缓存中</h2>
     * @param userId
     * @param status
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * <h2>尝试从Cache 中获取一个优惠券码</h2>
     * @param templateId
     * @return
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);
    /**
     * <h2>将优惠券保存到Cache中</h2>
     * @param userId
     * @param coupons
     * @param status
     * @return
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException;
}
