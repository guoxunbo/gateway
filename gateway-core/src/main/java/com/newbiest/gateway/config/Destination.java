package com.newbiest.gateway.config;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by guoxunbo on 2020-01-19 14:37
 */
@Data
public class Destination implements Serializable {

    public static final Integer DEFAULT_WEIGHT = 1;

    /**
     * 目标地址
     */
    private String destination;

    /**
     * 权重
     */
    private Integer weight = DEFAULT_WEIGHT;

    /**
     * 动态权重。不支持外部设值。
     */
    private Integer dynamicWeight = 0;

}
