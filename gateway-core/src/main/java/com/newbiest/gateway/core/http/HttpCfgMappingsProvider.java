package com.newbiest.gateway.core.http;

import com.newbiest.gateway.config.MappingProperties;
import com.newbiest.gateway.core.MappingsProvider;
import com.newbiest.gateway.core.MappingsValidator;

import java.util.List;

/**
 * 基于配置的HTTPProvider
 * Created by guoxunbo on 2019-12-20 15:28
 */
public class HttpCfgMappingsProvider extends MappingsProvider {

    private HttpClientProvider httpClientProvider;

    public HttpCfgMappingsProvider(MappingsValidator mappingsValidator, HttpClientProvider httpClientProvider, List<MappingProperties> mappings) {
        this.mappingsValidator = mappingsValidator;
        this.httpClientProvider = httpClientProvider;
        this.mappings = mappings;
    }
}
