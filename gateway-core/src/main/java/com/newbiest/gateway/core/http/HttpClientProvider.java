package com.newbiest.gateway.core.http;

import com.google.common.collect.Maps;
import com.newbiest.gateway.config.MappingProperties;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static org.apache.http.impl.client.HttpClientBuilder.create;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpClientProvider {

    protected Map<String, RestTemplate> httpClients = Maps.newHashMap();
    protected List<MappingProperties> mappings;

    public HttpClientProvider(List<MappingProperties> mappings) {
        this.mappings = mappings;
    }

    @PostConstruct
    public void init() {
        httpClients = mappings.stream().collect(toMap(MappingProperties::getName, this::createRestTemplate));
    }

    public RestTemplate getHttpClient(String mappingName) {
        return httpClients.get(mappingName);
    }

    protected RestTemplate createRestTemplate(MappingProperties mapping) {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(mapping.getConnectTimeOut());
        requestFactory.setReadTimeout(mapping.getReadTimeOut());
        return new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }


}
