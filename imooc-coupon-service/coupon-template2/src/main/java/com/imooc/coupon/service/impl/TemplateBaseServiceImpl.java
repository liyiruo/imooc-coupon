package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>优惠券模版基础服务接口实现</h1>
 */
@Service
@Slf4j
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private CouponTemplateDao templateDao;
    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * <h2>根据优惠券模版 id  获取优惠券模版信息</h2>
     * @param id 模版id
     * @return ｛@link CouponTemplate｝ 优惠券模版实体
     * @throws CompletionException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist:" + id);
        }
        return null;
    }
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> templates = templateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }
    /**
     * <h2>将CouponTemplate 转换为CouponTempSDK</h2>
     * @param template
     * @return
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
