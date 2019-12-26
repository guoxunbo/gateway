package com.newbiest.gateway.core.http;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

@Data
public class UnmodifiableRequestData {

    protected HttpMethod method;
    protected String uri;
    protected String host;
    protected HttpHeaders headers;
    protected byte[] body;
    protected HttpServletRequest originRequest;

    public UnmodifiableRequestData(RequestData requestData) {
        this(requestData.getMethod(), requestData.getHost(),
                requestData.getUri(), requestData.getHeaders(),
                requestData.getBody(), requestData.getOriginRequest());
    }

    public UnmodifiableRequestData(HttpMethod method, String host, String uri,
                                   HttpHeaders headers, byte[] body,HttpServletRequest request) {
        this.method = method;
        this.host = host;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
        this.originRequest = request;
    }

    public String getBodyAsString() {
        return StringUtils.convertBodyToString(body);
    }

}
