package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * <h1>kafka相关的服务接口定义</h1>
 */
public interface IKafkaService {
    /**
     * <h2>消费优惠券</h2>
     * @param record
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
