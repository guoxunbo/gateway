package com.newbiest.gateway.core;

import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.gateway.config.MappingProperties;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019-12-20 11:05
 */
@Slf4j
public abstract class MappingsProvider {

    protected MappingsValidator mappingsValidator;
    protected List<MappingProperties> mappings;

    public MappingProperties resolveMapping(String originHost, HttpServletRequest request) {
        List<MappingProperties> resolvedMappings = mappings.stream()
                .filter(mapping -> originHost.toLowerCase().equals(mapping.getHost().toLowerCase()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(resolvedMappings)) {
            return null;
        }
        return resolvedMappings.get(0);
    }
}
