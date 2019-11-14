package com.imooc.coupon.constant;

/**
 * <h1>常用常量定义</h1>
 *
 */

public class Constant {
    /**kafka 消息的TOPIC*/
    public static final String TOPIC = "imooc_user_coupon_op";
    /**
     * <h2> Redis key 前缀定义</h2>
     */
    public static class RedisPrefix{
        /**优惠券码 key 前缀*/
        public static final String COUPON_TEMPLATE =
                "imooc_coupon_template_code";
        /**用户当前所有可用的优惠券码 key 前缀*/
        public static final String USER_COUPON_TEMPLATE =
                "imooc_user_coupon_usable_";
        /**用户当前所有已使用的优惠券码 key 前缀*/
        public static final String USER_COUPON_USED =
                "imooc_user_coupon_used_";
        /**用户当前所有已过期的优惠券码 key 前缀*/
        public static final String USER_COUPON_EXPIRED =
                "imooc_user_coupon_expired_";
    }
}
