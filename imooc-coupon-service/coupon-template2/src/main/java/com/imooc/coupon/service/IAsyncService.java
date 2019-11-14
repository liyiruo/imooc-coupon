package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

public interface IAsyncService {
    /**
     * <h2>根据模版异步的创建优惠券码</h2>
     * @param template｛@link CouponTemplate｝
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
