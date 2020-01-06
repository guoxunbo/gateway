package com.newbiest.gateway.core.http;

import com.google.common.collect.Lists;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SystemPropertyUtils;
import com.newbiest.gateway.config.MappingProperties;
import com.newbiest.gateway.core.MappingsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
public class RequestForwarder {

    protected final HttpClientProvider httpClientProvider;
    protected final MappingsProvider mappingsProvider;

    public RequestForwarder(HttpClientProvider httpClientProvider, MappingsProvider mappingsProvider) {
        this.httpClientProvider = httpClientProvider;
        this.mappingsProvider = mappingsProvider;
    }

    public ResponseEntity<byte[]> forwardHttpRequest(RequestData data, MappingProperties mapping) {
        ForwardDestination destination = resolveForwardDestination(data.getUri(), mapping);
        prepareForwardedRequestHeaders(data, destination);

        if (log.isDebugEnabled()) {
            log.debug(String.format("Forwarding [%s] request to [%s] and the request string is \n %s", data.getMethod(), destination.getUri() + data.getUri(), data.getBodyString()));
        } else if (!log.isDebugEnabled() && log.isInfoEnabled()) {
            log.info("Forwarding request");
        }
        ResponseData response = sendRequest(mapping, data, destination.getUri());
        if (log.isDebugEnabled()) {
            log.debug(String.format("Forwarded and response status is [%s] and response string is \n %s", response.getStatus().value(), response.getBodyString()));
        } else if (!log.isDebugEnabled() && log.isInfoEnabled()) {
            log.info("Forwarded and get Response info");
        }
//        traceInterceptor.onForwardComplete(traceId, response.getStatus(), response.getBody(), response.getHeaders());
//        postForwardResponseInterceptor.intercept(response, mapping);
        prepareForwardedResponseHeaders(response);

        return status(response.getStatus())
                .headers(response.getHeaders())
                .body(response.getBody());

    }

    /**
     * Remove any protocol-level headers from the remote server's response that
     * do not apply to the new response we are sending.
     *
     * @param response
     */
    protected void prepareForwardedResponseHeaders(ResponseData response) {
        HttpHeaders headers = response.getHeaders();
        headers.remove(TRANSFER_ENCODING);
        headers.remove(CONNECTION);
        headers.remove("Public-Key-Pins");
        headers.remove(SERVER);
        headers.remove("Strict-Transport-Security");
    }

    protected void prepareForwardedRequestHeaders(RequestData request, ForwardDestination destination) {
        HttpHeaders headers = request.getHeaders();
        headers.remove(TE);
    }

    protected ForwardDestination resolveForwardDestination(String originUri, MappingProperties mapping) {
        return new ForwardDestination(createDestinationUrl(originUri, mapping), mapping.getName());
    }

    protected URI createDestinationUrl(String uri, MappingProperties mapping) {
//        String host = loadBalancer.chooseDestination(mapping.getDestinations());
        String host = mapping.getDestinations().get(0);
        try {
            return new URI(host + uri);
        } catch(URISyntaxException e) {
            throw new RuntimeException("Error creating destination URL from HTTP request URI: " + uri + " using mapping " + mapping, e);
        }
    }

    protected ResponseData sendFileRequest(MappingProperties mapping, RequestData requestData, URI uri) {
        ResponseEntity<byte[]> response = null;
        List<File> tempFiles = Lists.newArrayList();
        try {
            String tempFilePath = SystemPropertyUtils.getFileTempDir();
            Map<String, MultipartFile> multipartFileMap = ThreadLocalContext.getMultipartFileMap();
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<String, Object>();

            for (String parameterName : multipartFileMap.keySet()) {
                MultipartFile multipartFile = multipartFileMap.get(parameterName);
                String fileName = tempFilePath + multipartFile.getOriginalFilename();
                File tempFile = new File(fileName);
                multipartFile.transferTo(tempFile);
                FileSystemResource resource = new FileSystemResource(tempFile.getAbsolutePath());
                multiValueMap.add(parameterName, resource);
                tempFiles.add(tempFile);
            }
            multiValueMap.add("request", ThreadLocalContext.getRequest());
            RequestEntity<MultiValueMap<String,Object>> request = new RequestEntity<>(multiValueMap, requestData.getHeaders(), requestData.getMethod(), uri);

            response = httpClientProvider.getHttpClient(mapping.getName()).exchange(request, byte[].class);
        } catch (HttpStatusCodeException e) {
            response = status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        } finally {
            if (CollectionUtils.isNotEmpty(tempFiles)) {
                tempFiles.forEach(tempFile -> tempFile.deleteOnExit());
            }
        }
        return new ResponseData(response.getStatusCode(), response.getHeaders(), response.getBody(), requestData);
    }

    protected ResponseData sendBytesRequest(MappingProperties mapping, RequestData requestData, URI uri) {
        ResponseEntity<byte[]> response;
        try {
            RequestEntity<byte[]> request = new RequestEntity<>(requestData.getBody(), requestData.getHeaders(), requestData.getMethod(), uri);
            response = httpClientProvider.getHttpClient(mapping.getName()).exchange(request, byte[].class);
        } catch (HttpStatusCodeException e) {
            response = status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            throw e;
        }
        return new ResponseData(response.getStatusCode(), response.getHeaders(), response.getBody(), requestData);
    }

    protected ResponseData sendRequest(MappingProperties mapping, RequestData requestData, URI uri) {
        Map<String, MultipartFile> multipartFileMap = ThreadLocalContext.getMultipartFileMap();
        if (multipartFileMap != null && multipartFileMap.size() > 0) {
            return sendFileRequest(mapping, requestData, uri);
        } else {
            return sendBytesRequest(mapping, requestData, uri);
        }
    }

}
