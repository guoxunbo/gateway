package com.newbiest.gateway.ws.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author guoxunbo
 * @date 2020-08-29 12:37
 */
@XmlRootElement(name="Envelope")
@Data
public class SoapRequest  {

    private static final String DEFAULT_XMLNS = "http://schemas.xmlsoap.org/soap/envelope/";

    @XmlAttribute(name="xmlns")
    private String xmlns = DEFAULT_XMLNS;

    @XmlElement(name="Body")
    private SoapRequestBody body;

}
