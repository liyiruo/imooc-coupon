package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

/**
 * <h1>优惠券模版基础（view，delete……）服务定义</h1>
 */
public interface ITemplateBaseService {

    /**
     * <h2>根据优惠券模版id 获取优惠券模版信息</h2>
     * @param id 模版id
     * @return  CouponTemplate
     * @throws CompletionException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;
    /**
     * <h2>查找所有可能有用的优惠券模版</h2>
     * @return{@link CouponTemplateSDK}S
     */
    List<CouponTemplateSDK> findAllUsableTemplate();
    /**
     * <h2>获取模版 ids 到 CouponTemplateSDK的映射</h2>
     * @param ids
     * @return Map<key : 模版，value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);

}
