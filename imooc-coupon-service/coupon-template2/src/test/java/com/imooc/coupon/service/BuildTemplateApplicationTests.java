package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.vo.TemplateRequest;
import com.imooc.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * <h1>构造优惠券模版服务测试</h1>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildTemplateApplicationTests {
    @Autowired
    private IBuildTemplateService buildTemplateService;
    @Test
    public void testBuildTemplate() throws Exception {
        System.out.println(JSON.toJSONString(buildTemplateService
                .buildTemplate(fakeTemplateRequest())));
        Thread.sleep(5000);

    }

    private TemplateRequest fakeTemplateRequest() {
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模版"+new Date().getTime());
        request.setLogo("logo");
        request.setDesc("优惠券的描述");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(), 60).getTime()));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage("安徽省","桐城市", JSON.toJSONString(Arrays.asList("文娱","家居"))));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
