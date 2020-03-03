package com.newbiest.gateway.core.balancer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gateway.config.Destination;

import java.util.List;

/**
 * Created by guoxunbo on 2020-01-19 16:02
 */
public class HashLoadBalancer implements LoadBalancer {
    @Override
    public String getTargetDestination(List<Destination> destinationList) throws ClientException {
        return null;
    }
}
