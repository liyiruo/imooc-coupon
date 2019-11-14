package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * <h1>优惠券模版实体类自定义序列化器</h1>
 */
public class CouponTemplateSeriaztion
        extends JsonSerializer<CouponTemplate> {


    @Override
    public void serialize(CouponTemplate couponTemplate,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", couponTemplate.getId().toString());
        jsonGenerator.writeStringField("name", couponTemplate.getName());
        jsonGenerator.writeStringField("logo", couponTemplate.getLogo());
        jsonGenerator.writeStringField("desc", couponTemplate.getDesc());
        jsonGenerator.writeStringField("category", couponTemplate.getCategory().getDescription());
        jsonGenerator.writeStringField("productLine", couponTemplate.getProductLine().getDescription());
        jsonGenerator.writeStringField("count", couponTemplate.getCount().toString());
        jsonGenerator.writeStringField("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(couponTemplate.getCreateTime()));
        jsonGenerator.writeStringField("userId", couponTemplate.getUserid().toString());
        jsonGenerator.writeStringField("key", couponTemplate.getKey()+String.format("%04d")+couponTemplate.getId());
        jsonGenerator.writeStringField("target", couponTemplate.getTarget().getDescription());
        jsonGenerator.writeStringField("rule", JSON.toJSONString(couponTemplate.getRule()));

//        结束序列化对象
        jsonGenerator.writeEndObject();

    }
}
