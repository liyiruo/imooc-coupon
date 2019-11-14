package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理</h1>
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * <h2>对CouponException进行统一处理</h2>
     *
     * @param req
     * @param ex
     * @return
     */
    //这个注解 处理哪个异常
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest req, CouponException ex
    ) {
        CommonResponse<String> response = new CommonResponse<>(
                -1, "business error"
        );
        response.setDate(ex.getMessage());
        return response;
    }
}
