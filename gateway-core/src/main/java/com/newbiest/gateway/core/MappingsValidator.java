package com.newbiest.gateway.core;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gateway.config.MappingProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

public class MappingsValidator {

    public void validate(List<MappingProperties> mappings) {
        if (!isEmpty(mappings)) {
            mappings.forEach(this::validateMapping);
            int numberOfNames = mappings.stream()
                    .map(MappingProperties::getName)
                    .collect(toSet())
                    .size();
            if (numberOfNames < mappings.size()) {
                throw new ClientException("Duplicated route names in mappings");
            }
            int numberOfHosts = mappings.stream()
                    .map(MappingProperties::getHost)
                    .collect(toSet())
                    .size();
            if (numberOfHosts < mappings.size()) {
                throw new ClientException("Duplicated source hosts in mappings");
            }
            mappings.sort((mapping1, mapping2) -> mapping2.getHost().compareTo(mapping1.getHost()));
        }
    }

    protected void validateMapping(MappingProperties mapping) {
        validateName(mapping);
        validateDestinations(mapping);
        validateHost(mapping);
        validateTimeout(mapping);
    }

    protected void validateName(MappingProperties mapping) {
        if (StringUtils.isEmpty(mapping.getName())) {
            throw new ClientException("Empty name for mapping " + mapping);
        }
    }

    protected void validateDestinations(MappingProperties mapping) {
        if (isEmpty(mapping.getDestinations())) {
            throw new ClientException("No destination hosts for mapping" + mapping);
        }
        List<String> correctedHosts = new ArrayList<>(mapping.getDestinations().size());
        mapping.getDestinations().forEach(destination -> {
            if (StringUtils.isEmpty(destination)) {
                throw new ClientException("Empty destination for mapping " + mapping);
            }
            if (!destination.matches(".+://.+")) {
                destination = "http://" + destination;
            }
//            destination = removeEnd(destination, "/");
            correctedHosts.add(destination);
        });
        mapping.setDestinations(correctedHosts);
    }

    protected void validateHost(MappingProperties mapping) {
        if (StringUtils.isEmpty(mapping.getHost())) {
            throw new ClientException("No source host for mapping " + mapping);
        }
    }

    protected void validateTimeout(MappingProperties mapping) {
        int connectTimeout = mapping.getConnectTimeOut();

        if (connectTimeout < 0) {
            throw new ClientException("Invalid connect timeout value: " + connectTimeout);
        }
        int readTimeout = mapping.getReadTimeOut();
        if (readTimeout < 0) {
            throw new ClientException("Invalid read timeout value: " + readTimeout);
        }
    }
}
