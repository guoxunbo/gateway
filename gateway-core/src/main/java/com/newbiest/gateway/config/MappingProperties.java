package com.newbiest.gateway.config;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gateway.constant.GatewayException;
import com.newbiest.gateway.core.balancer.HashLoadBalancer;
import com.newbiest.gateway.core.balancer.LoadBalancer;
import com.newbiest.gateway.core.balancer.RoundRobinBalancer;
import lombok.Data;

import java.util.List;

/**
 * 配置转换关系的配置
 * Created by guoxunbo on 2019-12-20 10:29
 */
@Data
public class MappingProperties {

    public static final String LB_STRATEGY_ROUND_ROBIN = "RoundRobin";
    public static final String LB_STRATEGY_HASH = "Hash";

    /**
     * 名称 唯一
     */
    private String name;

    /**
     * 请求目标IP
     */
    private String host;

    /**
     *  LoadBalancer的策略
     */
    private String lbStrategy = LB_STRATEGY_ROUND_ROBIN;

    /**
     * 转发过去的地址
     */
    private List<Destination> destinations;

    private Integer connectTimeOut = 0;

    private Integer readTimeOut = 0;

    private LoadBalancer loadBalancer;

    protected LoadBalancer getLoadBalancer() throws ClientException {
        if (loadBalancer == null) {
            if (StringUtils.isNullOrEmpty(lbStrategy) || LB_STRATEGY_ROUND_ROBIN.equals(lbStrategy)) {
                loadBalancer =  new RoundRobinBalancer(destinations);
            } else if (LB_STRATEGY_HASH.equals(lbStrategy)) {
                loadBalancer = new HashLoadBalancer();
            } else {
                throw new ClientParameterException(GatewayException.LB_STRATEGY_IS_NOT_SUPPORTED, lbStrategy);
            }
        }
        return loadBalancer;
    }

}
