package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * <h1>用户服务相关的接口定义</h1>
 * 1.用户三类状态优惠券信息展示服务
 * 2.查看用户当前可以领取的优惠券模版
 * 3.用户领取优惠券服务
 * 4.用户消费优惠券服务
 */
public interface IUserService {
    /**
     * <h2>根据用户id 和状态查询优惠券记录</h2>
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     *
     * <h2>根据用户id 查找当前可以领取的优惠券模版</h2>
     * @param userId
     * @return
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * <h2>用户领取优惠券</h2>
     * @param request
     * @return
     * @throws CouponException
     */

    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;
    /**
     * <h2>结算核销优惠券</h2>
     * @param info
     * @return
     * @throws CouponException
     */
    SettlementInfo settleme(SettlementInfo info) throws CouponException;

}
