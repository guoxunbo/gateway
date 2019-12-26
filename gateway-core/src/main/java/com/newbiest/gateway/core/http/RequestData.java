package com.newbiest.gateway.core.http;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

@Data
public class RequestData extends UnmodifiableRequestData {
    private boolean needRedirect;
    private String redirectUrl;

    public RequestData(HttpMethod method,String host, String uri, HttpHeaders headers, byte[] body, HttpServletRequest request) {
        super(method, host, uri, headers, body, request);
    }

    public void setBody(String body) {
        this.body = StringUtils.convertStringToBody(body);
    }

}
