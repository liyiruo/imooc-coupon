package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum GoodsType {
    WENYU("文娱",1),
    SHENGXIAN("生鲜",2),
    OTHER("其他", 3),
    ALL("全品类",4);
    private String description;
    private  Integer code;

    private static GoodsType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(bean -> bean.code.equals(code)).findAny().orElseThrow(
                () -> new IllegalArgumentException(code + "code is not exist")
        );
    }
}
