package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IKafkaService;
import com.imooc.coupon.vo.CouponKafakaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.validator.constraints.EAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <h1>kafaka 相关的服务接口实现</h1>
 * 核心思想：是将cache 中的Coupon 的状态变化同步到 DB 中
 */
@Service//@Component
@Slf4j
public class KafkaServiceImpl implements IKafkaService {
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * <h2>消费优惠券，kafka消息</h2>
     *
     * @param record
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            //序列化把message 序列化为一个对象
            CouponKafakaMessage couponInfo = JSON.parseObject(
                    message.toString(), CouponKafakaMessage.class
            );
            log.info("Receive CouponKafkaMessage:{}", message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }
    }


    /**
     * <h2>处理已使用的优惠券</h2>
     */
    private void processUsedCoupons(CouponKafakaMessage kafakaMessage,
                                    CouponStatus status) {
        //TODO 给用户发送短信
        processCouponsByStatus(kafakaMessage, status);
    }

    /**
     * <h2>处理过期的用户优惠券</h2>
     */
    private void processExpiredCoupons(CouponKafakaMessage kafakaMessage,
                                       CouponStatus status) {
        //TODO 给用户发送推送
        processCouponsByStatus(kafakaMessage, status);
    }

    /**
     * <h2>根据状态处理优惠券信息</h2>
     *
     * @param kafakaMessage
     * @param status
     */
    private void processCouponsByStatus(CouponKafakaMessage kafakaMessage,
                                        CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafakaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafakaMessage.getIds().size()) {
            log.error("can not find right coupon info {}", JSON.toJSONString(kafakaMessage));
            //TODO 发送邮件
            return;
        }
        coupons.forEach(c -> c.setStatus(status));
        log.info("couponkafkamessage op coupon count:{}", couponDao.saveAll(coupons).size());
    }
}
