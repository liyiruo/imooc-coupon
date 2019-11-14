package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>Redis 相关的操作服务接口实现</h1>
 */
@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * <h2>根据userId 和状态找到缓存的优惠券列表数据</h2>
     *
     * @param userId
     * @param status
     * @return
     */
    @Override
    public List<Coupon> getCachedCoupon(Long userId, Integer status) {
        log.info("Get Coupons From Cache:{}{}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey).stream().map(o -> Objects.toString(o, null)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream().map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    /**
     * <h2>保存空的优惠券列表到缓存中</h2>
     * 目的：避免缓存穿透
     *
     * @param userId 用户Id
     * @param status 优惠券状态列表
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("save empty list to cache for user:{},status:{}", userId, status);
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSONObject.toJSONString(Coupon.invalidCoupon()));
        //使用SessionCallback 把数据命令放入到Redis的pipeline
        //一次发送
        //用户优惠券
        //KV
        //K:status -> redisKeyCoupon}

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    operations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipline Exe Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * <h2>尝试从Cache 中获取一个优惠券码</h2>
     *
     * @param templateId
     * @return 优惠券码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        //拼接redisKey
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        //因为优惠券码不存在顺序关系，左边pop或右边，没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code:{}{}{}", templateId, redisKey, couponCode);

        return null;
    }

    /**
     * <h2>将用户的优惠券保存到cache中</h2>
     *
     * @param userId
     * @param coupons
     * @param status
     * @return
     * @throws CouponException
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{}{}{}", userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                result = addCouoponToCacheForUsable(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * <h2>新增加优惠券到Cache中</h2>
     *
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouoponToCacheForUsable(Long userId, List<Coupon> coupons) {

        //如果status 是USABLE ,代表是增加的优惠券
        //只会影响一个Cache:USER_COUPON_USABLE
        log.debug("Add coupon to cache for usable");
        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c -> needCachedObject.put(c.getId().toString(), JSON.toJSONString(c)));
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("add{}coupons to  cache{},{}", needCachedObject.size(), userId, redisKey);
        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1, 2),
                TimeUnit.SECONDS
        );
        return needCachedObject.size();
    }

    /**
     * <h2>将已使用的优惠券加入到Cache中</h2>
     *
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        //如果 status 是 USED, 代表用户操作是使用当前的优惠券，影响到两个Cache
        //USABLE,USED 只有可用，才可以被使用
        log.debug("add coupon to cache for used");
        Map<String, String> needCacheForUsed = new HashMap<>(coupons.size());
        String redisKeyUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);
        //获取当前用户可用优惠券
        List<Coupon> curUsableCoupons = getCachedCoupon(userId, CouponStatus.USABLE.getCode());
        //当前可用的优惠券个数一定是大于1
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c -> needCacheForUsed.put(c.getId().toString(), JSON.toJSONString(c)));
        //校验当前的优惠券参数是否与Cached中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("curcoupons is not equal tocache:{}{}{}", userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("curcoupons is not equal to cache");
        }
        List<String> needCleanKey = paramIds.stream().map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //1.已使用的优惠券 cache 缓存
                operations.opsForHash().putAll(redisKeyUsed, needCacheForUsed);
                //2.可用的优惠券cache需要清理
                operations.opsForHash().delete(redisKeyUsable, needCleanKey.toArray());
                //3.重置过期时间
                operations.expire(redisKeyUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }

        };
        log.info("pipeline exe result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * <h2>将过期优惠券加入到cache中</h2>
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    private Integer addCouponToCacheForExpired(Long userId,List<Coupon>coupons)throws CouponException {
        //status 是 EXPIRED,代表是已有的优惠券过期了
        //营销到2个Cache   USABLE ,EXPIRED
        log.debug("add coupon to cache for expired");
        //最终需要保存的Cache
        Map<String, String> needCacheForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);
        List<Coupon> curUsableCoupons = getCachedCoupon(userId, CouponStatus.USABLE.getCode());
        List<Coupon> curExpiredCoupons = getCachedCoupon(userId, CouponStatus.EXPIRED.getCode());
        //当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c -> needCacheForExpired.put(c.getId().toString(), JSON.toJSONString(c)));

        //校验当前的优惠券参数是否与 Cached中的匹配
        List<Integer>curUsableIds=curUsableCoupons.stream().map(Coupon::getId).collect(Collectors.toList());
        List<Integer>paramIds=coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        //第一个参数是第二参数的子集吗
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("corcoupons is not equal o cache:{}{}{}", userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("curcoupon is not equal to cache");
        }
        List<String> needCleanKey = paramIds.stream().map(i -> i.toString()).collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //1. 已经过期的优惠券缓存
                operations.opsForHash().putAll(redisKeyForExpired, needCacheForExpired);
                //2.可用的优惠券cache需要清理
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());
                //重置过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                operations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("pipeline exe result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();

    }





    /**
     * <h2>根据status 获取到对应的Redis Key</h2>
     *
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_TEMPLATE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间</h2>
     *
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 【min,max】之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }

}
