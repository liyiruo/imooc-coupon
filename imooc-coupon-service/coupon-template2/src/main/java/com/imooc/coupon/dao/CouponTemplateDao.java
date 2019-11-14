package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>CouponTem</h1>
 */
public interface CouponTemplateDao
        extends JpaRepository<CouponTemplate, Integer> {
    /**
     * <h2>根据模版名称查询模版</h2>
     *
     * @param name
     * @return
     */
    CouponTemplate findByName(String name);

    /**
     * <h2>根据available 和expired 标记查找模版记录</h2>
     *
     * @param available
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    /**
     * <h2>根据expired标记查找模版记录</h2>
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}