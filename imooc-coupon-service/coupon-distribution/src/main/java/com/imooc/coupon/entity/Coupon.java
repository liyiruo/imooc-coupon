package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.converter.CouponStatusConverter;
import com.imooc.coupon.serialization.CouponSerialize;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * <h1>用户领取的优惠券的实体表</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="coupon")
//下面这个注解是序列化器
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template",nullable = false)
    private Integer id;
    @Column(name = "template_id",nullable = false)
    private Integer templateId;
    @Column(name = "user_id",nullable = false)
    private Long userId;
    @Column(name = "coupon_code",nullable = false)
    private String couponCode;
    @CreatedDate
    @Column(name = "assign_time",nullable = false)
    private Date assignTime;

    @Column(name = "status",nullable = false)
//    下面这个注解是转换器
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;
    @Transient
    private CouponTemplateSDK templateSDK;
    /**
     * <h2>返回一个无效的Coupon对象</h2>
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }
    public Coupon( Integer templateId,
                   Long userId,
                   String couponCode,
                   CouponStatus status) {
        this.templateId = templateId;
        this.couponCode=couponCode;
        this.userId=userId;
        this.status = status;
    }
}
