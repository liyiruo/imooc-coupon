package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <h1>构建优惠券模版接口实现</h1>
 */
@Slf4j
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    private final IAsyncService asyncService;
    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * <h2>创建优惠券模版</h2>
     *
     * @param request ｛@link TemplateRequest｝模版信息请求对象
     * @return ｛@link CoupTemplate｝ 优惠券模版实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        //校验参数的合法性
        if (!request.validate()) {
            throw new CouponException("BuildTemplate Param Is not valid");
        }
        //判断同名的优惠券模版是否存在
        if (null != templateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template");
        }
        //构造CouponTemplage 并保存到数据库中

        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);
        //根据优惠券模版异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    /**
     * <h2>将TemplateRequest 转换为 CouponTemplate</h2>
     *
     * @param request
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
