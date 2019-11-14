package com.imooc.coupon.schedule;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1></h1>
 */
@Slf4j
@Component
public class ScheduledTask {
    private final CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }


    /**
     * <h2>下线已过期的优惠券模版</h2>
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplage() {
        log.info("start to expire coupontemplate");
        /*查询未过期的优惠券*/
        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Done to expire couponTemplate.");
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.forEach(t -> {
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadline() < cur.getTime()) {
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });
        if (CollectionUtils.isEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num:{}", templateDao.saveAll(expiredTemplates));
        }
    }
}
