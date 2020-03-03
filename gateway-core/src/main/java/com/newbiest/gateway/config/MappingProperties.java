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

    private String name;

    private String host;

    private String lbStaStrategy = LB_STRATEGY_ROUND_ROBIN;

    private List<Destination> destinations;

    private Integer connectTimeOut = 0;

    private Integer readTimeOut = 0;

    private LoadBalancer loadBalancer;

    protected LoadBalancer getLoadBalancer() throws ClientException {
        if (loadBalancer == null) {
            if (StringUtils.isNullOrEmpty(lbStaStrategy) || LB_STRATEGY_ROUND_ROBIN.equals(lbStaStrategy)) {
                loadBalancer =  new RoundRobinBalancer(destinations);
            } else if (LB_STRATEGY_HASH.equals(lbStaStrategy)) {
                loadBalancer = new HashLoadBalancer();
            } else {
                throw new ClientParameterException(GatewayException.LB_STRATEGY_IS_NOT_SUPPORTED, lbStaStrategy);
            }
        }
        return loadBalancer;
    }

}
