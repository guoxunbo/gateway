package com.newbiest.gateway.core.http;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Data
public class ResponseData {
    protected HttpStatus status;
    protected HttpHeaders headers;
    protected byte[] body;
    protected RequestData requestData;

    public ResponseData(HttpStatus status, HttpHeaders headers, byte[] body, RequestData requestData) {
        this.status = status;
        this.headers = new HttpHeaders();
        this.headers.putAll(headers);
        this.body = body;
        this.requestData = requestData;
    }

    public String getBodyString() {
        return StringUtils.convertBodyToString(body);
    }


}
