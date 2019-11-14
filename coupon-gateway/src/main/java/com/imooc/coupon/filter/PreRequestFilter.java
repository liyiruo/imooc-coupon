package com.imooc.coupon.filter;

/**
 * <h1>在过滤器中存储客户端发起请求的时间戳</h1>
 */
public class PreRequestFilter extends  AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
