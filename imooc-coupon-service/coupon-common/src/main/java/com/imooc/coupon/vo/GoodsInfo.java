package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>fake 商品信息</h1>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {
    private Integer type;
    private Double price;
    private Integer count;
    //TODO 名称
}
