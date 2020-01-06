package com.newbiest.gateway.core.http;

import lombok.Data;

import java.net.URI;

@Data
public class ForwardDestination {

    protected final URI uri;
    protected final String mappingName;

    public ForwardDestination(URI uri, String mappingName) {
        this.uri = uri;
        this.mappingName = mappingName;
    }

}
