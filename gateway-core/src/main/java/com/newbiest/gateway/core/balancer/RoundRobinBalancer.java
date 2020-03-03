package com.newbiest.gateway.core.balancer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gateway.config.Destination;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平滑加权轮询算法
 * Created by guoxunbo on 2020-01-19 15:12
 */
public class RoundRobinBalancer implements LoadBalancer{

    private Map<String, Destination> weightMap;

    private int totalWeight;

    public RoundRobinBalancer(List<Destination> destinations) {
        // 创建的时候，将动态权重设置为0
        weightMap = destinations.stream().map(destination -> {
            destination.setDynamicWeight(0);
            return destination;
        }).collect(Collectors.toConcurrentMap(Destination :: getDestination, Function.identity()));
        totalWeight = destinations.stream().collect(Collectors.summingInt(Destination :: getWeight));
    }

    @Override
    public String getTargetDestination(List<Destination> destinationList) throws ClientException {
        for (Destination destination : weightMap.values()) {
            destination.setDynamicWeight(destination.getWeight() + destination.getDynamicWeight());
        }
        // 倒序排序获取到最大的
        Destination maxWeight =  weightMap.values().stream().sorted(
                                            Comparator.comparing(Destination :: getWeight).reversed()).findFirst().get();
        maxWeight.setDynamicWeight(maxWeight.getDynamicWeight() - totalWeight);

        return maxWeight.getDestination();
    }


}
