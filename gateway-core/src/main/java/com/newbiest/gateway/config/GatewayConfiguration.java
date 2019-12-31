package com.newbiest.gateway.config;

import com.newbiest.base.http.filter.LogFilter;
import com.newbiest.gateway.core.MappingsProvider;
import com.newbiest.gateway.core.MappingsValidator;
import com.newbiest.gateway.core.http.HttpCfgMappingsProvider;
import com.newbiest.gateway.core.http.HttpClientProvider;
import com.newbiest.gateway.core.http.RequestForwarder;
import com.newbiest.gateway.core.http.filter.ReverseProxyFilter;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Gateway的核心类
 * 此核心只针对于Http。其他协议只需要注册listener
 * Created by guoxunbo on 2019-12-20 10:51
 */
@Configuration
@ConfigurationProperties("gateway")
@Import(value = GatewayRestConfig.class)
@Data
public class GatewayConfiguration {

    public GatewayConfiguration() {
    }

    private List<MappingProperties> mappings = new ArrayList<>();

    @Bean
    public FilterRegistrationBean<ReverseProxyFilter> reverseProxyFilterRegistrationBean(ReverseProxyFilter proxyFilter) {
        FilterRegistrationBean<ReverseProxyFilter> registrationBean = new FilterRegistrationBean<>(proxyFilter);
        registrationBean.setOrder(HIGHEST_PRECEDENCE + 100);
        return registrationBean;
    }



    @Bean
    @ConditionalOnMissingBean
    public RequestForwarder faradayRequestForwarder(
            HttpClientProvider httpClientProvider,
            MappingsProvider mappingsProvider) {
        return new RequestForwarder(httpClientProvider, mappingsProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReverseProxyFilter faradayReverseProxyFilter(MappingsProvider mappingsProvider,
                                                        RequestForwarder requestForwarder) {
        return new ReverseProxyFilter(mappingsProvider, requestForwarder);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientProvider httpClientProvider() {
        return new HttpClientProvider(mappings);
    }



    @Bean
    @ConditionalOnMissingBean
    public MappingsValidator mappingsValidator() {
        return new MappingsValidator();
    }


    @Bean
    @ConditionalOnMissingBean
    public MappingsProvider httpCfMappingProvider(HttpClientProvider httpClientProvider, MappingsValidator mappingsValidator) {
        return new HttpCfgMappingsProvider(mappingsValidator, httpClientProvider, mappings);
    }
}
