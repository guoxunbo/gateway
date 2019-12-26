package com.newbiest.gateway.config;

import lombok.Data;

import java.util.List;

/**
 * 配置转换关系的配置
 * Created by guoxunbo on 2019-12-20 10:29
 */
@Data
public class MappingProperties {

    private String name;

    private String host;

    private List<String> destinations;

    private Integer connectTimeOut = 0;

    private Integer readTimeOut = 0;

}
