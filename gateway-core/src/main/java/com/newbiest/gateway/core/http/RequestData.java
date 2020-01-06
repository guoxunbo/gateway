package com.newbiest.gateway.core.http;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Data
public class RequestData implements Serializable {

    protected HttpMethod method;
    protected String uri;
    protected String host;
    protected HttpHeaders headers;
    protected byte[] body;

    private boolean needRedirect;
    private String redirectUrl;

    public RequestData(HttpMethod method, String host, String uri, HttpHeaders headers, byte[] body) {
        this.method = method;
        this.host = host;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public void setBody(String body) {
        this.body = StringUtils.convertStringToBody(body);
    }

    public String getBodyString() {
        return StringUtils.convertBodyToString(body);
    }

}
