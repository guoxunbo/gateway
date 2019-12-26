package com.newbiest.gateway.core.http.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.msg.DefaultRequest;
import com.newbiest.base.msg.Request;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.HttpUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.wrapper.HttpServletRequestReaderWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 记录所有请求的日志
 * Created by guoxunbo on 2019-12-24 17:52
 */
@Slf4j
public class LogFilter extends OncePerRequestFilter {

    /**
     * Upload类型的时候，requestString不一样
     */
    public static final String CONTENT_TYPE_UPLOAD = "multipart/form-data";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        httpServletRequest.getMethod();

        String origin = HttpUtils.getOrigin(httpServletRequest);
        if (!StringUtils.isNullOrEmpty(origin)) {
            HttpUtils.buildCorsAcross(response, origin);
        }

        if (httpServletRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return;
        }

        StringBuffer logBuffer = new StringBuffer();

        String ipAddress = HttpUtils.getClientIpAddress(httpServletRequest);
        String osAndBrowser = HttpUtils.getHeadUserAgent(httpServletRequest);
        HttpServletRequestReaderWrapper requestWrapper = new HttpServletRequestReaderWrapper(httpServletRequest);

        String requestString = HttpUtils.getBodyString(requestWrapper);

        String contentType = requestWrapper.getContentType();
        if (!StringUtils.isNullOrEmpty(contentType) && contentType.contains(CONTENT_TYPE_UPLOAD)) {
            StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(requestWrapper);
            Map map = multipartRequest.getParameterMap();
            requestString = ((String[]) map.get("request"))[0];
        }
        ThreadLocalContext.putRequest(requestString);
        logBuffer.append("Ip [" + ipAddress + "] at os and browser info [" + osAndBrowser + "] send " + "[" + httpServletRequest.getMethod() + "] request!");
        if (log.isDebugEnabled()) {
            logBuffer.append("The request string is \n");
            logBuffer.append(requestString);
            log.debug(logBuffer.toString());
        } else if (!log.isDebugEnabled() && log.isInfoEnabled()) {
            ObjectMapper objectMapper = DefaultParser.getObjectMapper();
            Request requestModel = objectMapper.readValue(requestString, DefaultRequest.class);
            logBuffer.append("The request transactionId is [" + requestModel.getHeader().getTransactionId() + "]");
            log.info(logBuffer.toString());
        }
        filterChain.doFilter(requestWrapper, response);
    }

}
