package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <h1>优惠券模版创建请求对象</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {


    /**
     * 优惠券的名称
     */
    private String name;

    private String logo;

    private String desc;

    private String category;

    private Integer productLine;

    private Integer count;

    private Long userId;

    private Integer target;
    /**
     * 优惠券规则
     */
    private TemplateRule rule;

    public boolean validate() {
        boolean stringVali= StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);

        boolean enumvali = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid=count>0&&userId>0;
        return stringVali && enumvali && numValid;
    }

}
