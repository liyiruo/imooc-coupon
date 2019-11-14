package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务实现接口</h1>
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    /***/
   private final CouponTemplateDao templateDao;

   private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }



    /**
     * <h2>根据模板异步的创建优惠券码</h2>
     * @param template｛@link CouponTemplate｝优惠券模板实体
     */
    @Override
    @Async("getAsyncExecutor")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis:{}",redisTemplate.opsForList().rightPushAll(redisKey,couponCodes));

        template.setAvailable(true);
        templateDao.save(template);
        stopwatch.stop();
        log.info("counstruct Coupon by Template Cost:{}ms",stopwatch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 执行完毕 发送短信或者邮件通知优惠券模版已可用
    }

    /**
     * <h2>构造优惠券码</h2>
     * 优惠券码（对应于每一张优惠券，18位）
     * @param template ｛@link CouponTemplate｝实体类
     * @return
     * 前四位：产品线+类型
     * 中间6位：日期的随机（190101）
     * 后8位：0-9的随机数构成
     */

    private Set<String> buildCouponCode(CouponTemplate template) {
//        计时器
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());
//        前四位
        String prefix4=template.getProductLine().getCode().toString()+template.getCategory().getCode();
        //
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        for (int i = 0; i!=template.getCount() ; i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        watch.stop();
        log.info("Build coupon code Cost:{}ms",watch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * <h2>构造优惠券的后14位</h2>
     * @param date 创建优惠券的日期
     * @return 14 位 优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1','2','3','4','5','6','7','8','9'};
        // 中间6位
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6=chars.stream().map(Objects::toString).collect(Collectors.joining());
        //后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }

}
