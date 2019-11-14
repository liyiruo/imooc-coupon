package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模版相关的功能控制器</h1>
 */
@Slf4j
@RestController
public class CouponTemplateController {
    private final IBuildTemplateService buildTemplateService;
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService,
                                    ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * <h2>构建优惠券模版</h2>
     *
     * @param request
     * @return
     * @throws CouponException 127.0.01:7001/coupon-template/template/build
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template:{}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    @GetMapping("/templat/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException{
        log.info("Build Template Info For:{}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可能可用的优惠券模版</h2>
     * @return
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }
    /**
     * <h2>获取模版 ids 到CouponTemplateSDK 的映射</h2>
     * @param ids
     * @return
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer>ids) {
        log.info("FindIds2TemplateSDK:{}",JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
