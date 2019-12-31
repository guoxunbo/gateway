package com.newbiest.gateway.core.http.filter;

import com.google.common.base.Stopwatch;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.HttpUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gateway.config.MappingProperties;
import com.newbiest.gateway.core.MappingsProvider;
import com.newbiest.gateway.core.http.RequestData;
import com.newbiest.gateway.core.http.RequestForwarder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 收到请求之后开始进行转发
 */
@Slf4j
public class ReverseProxyFilter extends OncePerRequestFilter {

    protected static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    protected static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
    protected static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
    protected static final String X_FORWARDED_PORT_HEADER = "X-Forwarded-Port";

    protected final MappingsProvider mappingsProvider;
    protected final RequestForwarder requestForwarder;

    public ReverseProxyFilter(MappingsProvider mappingsProvider, RequestForwarder requestForwarder) {
        this.mappingsProvider = mappingsProvider;
        this.requestForwarder = requestForwarder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String originUri = HttpUtils.getUri(request);
        String originHost = HttpUtils.getHost(request);

        HttpHeaders headers = HttpUtils.getHeaders(request);
        HttpMethod method = HttpUtils.getHttpMethod(request);

        // 读取传递的文件内容
        byte[] body = StringUtils.convertStringToBody(ThreadLocalContext.getRequest());
        MappingProperties mapping = mappingsProvider.resolveMapping(originHost, request);
        if (mapping == null) {
            log.debug(String.format("Forwarding: %s %s %s -> no mapping found", method, originHost, originUri));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Unsupported domain");
            return;
        } else {
            log.debug(String.format("Forwarding: %s %s %s -> %s", method, originHost, originUri, mapping.getDestinations()));
        }
        addForwardHeaders(request, headers);

        RequestData dataToForward = new RequestData(method, originHost, originUri, headers, body, request);
//        preForwardRequestInterceptor.intercept(dataToForward, mapping);
        if (dataToForward.isNeedRedirect() && !StringUtils.isNullOrEmpty(dataToForward.getRedirectUrl())) {
            log.debug(String.format("Redirecting to -> %s", dataToForward.getRedirectUrl()));
            response.sendRedirect(dataToForward.getRedirectUrl());
            return;
        }

        ResponseEntity<byte[]> responseEntity = requestForwarder.forwardHttpRequest(dataToForward, mapping);
        this.processResponse(response, responseEntity);
    }

    protected void addForwardHeaders(HttpServletRequest request, HttpHeaders headers) {
        List<String> forwordedFor = headers.get(X_FORWARDED_FOR_HEADER);
        if (isEmpty(forwordedFor)) {
            forwordedFor = new ArrayList<>(1);
        }
        forwordedFor.add(request.getRemoteAddr());
        headers.put(X_FORWARDED_FOR_HEADER, forwordedFor);
        headers.set(X_FORWARDED_PROTO_HEADER, request.getScheme());
        headers.set(X_FORWARDED_HOST_HEADER, request.getServerName());
        headers.set(X_FORWARDED_PORT_HEADER, valueOf(request.getServerPort()));
    }


    protected void processResponse(HttpServletResponse response, ResponseEntity<byte[]> responseEntity) {
        response.setStatus(responseEntity.getStatusCode().value());
        responseEntity.getHeaders().forEach((name, values) ->
                values.forEach(value -> response.addHeader(name, value))
        );
        if (responseEntity.getBody() != null) {
            try {
                response.getOutputStream().write(responseEntity.getBody());
            } catch (IOException e) {
                throw new RuntimeException("Error writing body of HTTP response", e);
            }
        }
    }
}
