package com.newbiest.gateway.core.balancer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gateway.config.Destination;

import java.util.List;

/**
 * LoadBalancer的策略s
 * Created by guoxunbo on 2020-01-19 14:02
 */
public interface LoadBalancer {

    String getTargetDestination(List<Destination> destinationList) throws ClientException;

}
