package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.converter.CouponCategoryConverter;
import com.imooc.coupon.serialization.CouponTemplateSeriaztion;
import com.imooc.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h1>优惠券模版实体定义，基础属性+规则属性</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSeriaztion.class)//序列化器
public class CouponTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
//    @Transient//不与数据库对应需要加这个注解
    private Integer id;
    @Column(name = "available", nullable = false)
    private Boolean available;
    /**
     * 是否过期
     */
    @Column(name = "expired", nullable = false)
    private Boolean expired;
    /**
     * 优惠券的名称
     */
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "logo", nullable = false)
    private String logo;
    @Column(name = "intro", nullable = false)
    private String desc;
    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;
    @Column(name = "product_line",nullable = false)
    @Convert(converter=ProductLine.class)
    private ProductLine productLine;
    /**总数*/
    @Column(name = "coupon_count",nullable = false)
    private Integer count;
    /**创建时间*/
    @CreatedDate
    @UpdateTimestamp
    @CreatedBy
    @Column(name = "create_time",nullable = false)
    private Date createTime;
    /**创建用户*/
    @Column(name = "user_id",nullable = false)
    private Long userid;
    /**优惠券模版编码*/
    @Column(name = "template_key",nullable = false)
    private String key;
    /**目标用户*/
    @Column(name = "target",nullable = false)
    @Convert(converter = DistributeTarget.class)
    private DistributeTarget target;
    @Column(name = "rule",nullable = false)
    @Convert(converter = TemplateRule.class)
    private TemplateRule rule;

    public CouponTemplate(String name,
                          String logo,
                          String desc,
                          String category,
                          Integer productLine,
                          Integer count,
                          Long userid,
                          Integer target,
                          TemplateRule rule) {
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userid = userid;
        this.target = DistributeTarget.of(target);
        this.rule = rule;
        this.available = false;
        this.expired=false;
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
