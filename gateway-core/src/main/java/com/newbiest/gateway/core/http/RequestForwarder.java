package com.newbiest.gateway.core.http;

import com.newbiest.gateway.config.MappingProperties;
import com.newbiest.gateway.core.MappingsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.System.nanoTime;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
public class RequestForwarder {

//    protected final ServerProperties serverProperties;
    protected final HttpClientProvider httpClientProvider;
    protected final MappingsProvider mappingsProvider;
//    protected final LoadBalancer loadBalancer;
//    protected final Optional<MeterRegistry> meterRegistry;
//    protected final ProxyingTraceInterceptor traceInterceptor;
//    protected final PostForwardResponseInterceptor postForwardResponseInterceptor;

    public RequestForwarder(
            HttpClientProvider httpClientProvider,
            MappingsProvider mappingsProvider
//            Optional<MeterRegistry> meterRegistry
    ) {
        this.httpClientProvider = httpClientProvider;
        this.mappingsProvider = mappingsProvider;
//        this.meterRegistry = meterRegistry;
    }

    public ResponseEntity<byte[]> forwardHttpRequest(RequestData data, MappingProperties mapping) {
        ForwardDestination destination = resolveForwardDestination(data.getUri(), mapping);
        prepareForwardedRequestHeaders(data, destination);
//        traceInterceptor.onForwardStart(traceId, destination.getMappingName(),
//                data.getMethod(), data.getHost(), destination.getUri().toString(),
//                data.getBody(), data.getHeaders());
        RequestEntity<byte[]> request = new RequestEntity<>(data.getBody(), data.getHeaders(), data.getMethod(), destination.getUri());
        ResponseData response = sendRequest(request, mapping, destination.getMappingMetricsName(), data);

        log.debug(String.format("Forwarded: %s %s %s -> %s %d", data.getMethod(), data.getHost(), data.getUri(), destination.getUri(), response.getStatus().value()));

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

    /**
     * Remove any protocol-level headers from the clients request that
     * do not apply to the new request we are sending to the remote server.
     *
     * @param request
     * @param destination
     */
    protected void prepareForwardedRequestHeaders(RequestData request, ForwardDestination destination) {
        HttpHeaders headers = request.getHeaders();
        //headers.set(HOST, destination.getUri().getAuthority());
        headers.remove(TE);
    }

    protected ForwardDestination resolveForwardDestination(String originUri, MappingProperties mapping) {
        return new ForwardDestination(createDestinationUrl(originUri, mapping), mapping.getName(), resolveMetricsName(mapping));
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

    protected ResponseData sendRequest(RequestEntity<byte[]> request, MappingProperties mapping, String mappingMetricsName, RequestData requestData ) {
        ResponseEntity<byte[]> response;
        long startingTime = nanoTime();
        try {
            response = httpClientProvider.getHttpClient(mapping.getName()).exchange(request, byte[].class);
            recordLatency(mappingMetricsName, startingTime);
        }
//        catch (HttpStatusCodeException e) {
//            recordLatency(mappingMetricsName, startingTime);
//            response = status(e.getStatusCode())
//                    .headers(e.getResponseHeaders())
//                    .body(e.getResponseBodyAsByteArray());
//            throw e;
//        }
        catch (Exception e) {
            recordLatency(mappingMetricsName, startingTime);
//            traceInterceptor.onForwardFailed(traceId, e);
            throw e;
        }
        UnmodifiableRequestData data = new UnmodifiableRequestData(requestData);
        return new ResponseData(response.getStatusCode(), response.getHeaders(), response.getBody(), data);
    }

    protected void recordLatency(String metricName, long startingTime) {
//        meterRegistry.ifPresent(meterRegistry -> meterRegistry.timer(metricName).record(ofNanos(nanoTime() - startingTime)));
    }

    protected String resolveMetricsName(MappingProperties mapping) {
        return "testMetrics";
//        return faradayProperties.getMetrics().getNamesPrefix() + "." + mapping.getName();
    }
}
