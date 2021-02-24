package com.newbiest.gateway.ws;

import com.newbiest.gateway.ws.service.WSApiService;
import com.newbiest.gateway.ws.service.WSApiServiceTest;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * WebService 相关配置
 * @author guoxunbo
 * @date 2020-07-27 17:31
 */
@Configuration
public class WebServiceConfig {

    public static final String DEFAULT_TARGET_NAMED_SPACE  = "http://www.vcimsoft.com";
    public static final String DEFAULT_NAME  = "VCIMWS";

    public static final String DEFAULT_WS_ROOT_MAPPING = "/APIWebService/*";

    @Bean("WebServiceRegistration")
    public ServletRegistrationBean dispatcherServlet(){
        return new ServletRegistrationBean(new CXFServlet(), DEFAULT_WS_ROOT_MAPPING);
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return  new SpringBus();
    }

    @Bean
    public Endpoint endpoint( ) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), new WSApiService());
        endpoint.publish("/portal");
        return endpoint;
    }

    @Bean
    public Endpoint endpoint2( ) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), new WSApiServiceTest());
        endpoint.publish("/portal2");
        return endpoint;
    }

}
