package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.TemplateRequest;

/**
 * <h1>构建优惠券模版接口定义</h1>
 */
public interface IBuildTemplateService {
    /**
     * <h2>创建优惠券模版</h2>
     * @param request ｛@link TemplateRequest｝模版信息请求对象
     * @return ｛@link CouponTemplate｝优惠券模版实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
