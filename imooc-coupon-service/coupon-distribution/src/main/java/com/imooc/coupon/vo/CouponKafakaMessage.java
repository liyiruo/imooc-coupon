package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>优惠券kafaka 消息对象定义</h1>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafakaMessage {
    /**优惠券状态*/
    private Integer status;
    /** Coupon 主键*/
    private List<Integer> ids;


}
