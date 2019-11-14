package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券的状态</h1>
 */
@Getter
@AllArgsConstructor
public enum  CouponStatus {
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的（未被使用）", 3);

    private String description;
    private Integer code;


    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code)).findAny().orElseThrow(() -> new IllegalArgumentException(code+"not exist"));
    }
}
