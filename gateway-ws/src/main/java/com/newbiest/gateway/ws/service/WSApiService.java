package com.newbiest.gateway.ws.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultResponse;
import com.newbiest.base.msg.Request;
import com.newbiest.base.msg.Response;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.gateway.ws.WebServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author guoxunbo
 * @date 2020-07-27 17:15
 */
@Service
@WebService(
        targetNamespace = WebServiceConfig.DEFAULT_TARGET_NAMED_SPACE,
        name = WebServiceConfig.DEFAULT_NAME,
        serviceName = WebServiceConfig.DEFAULT_NAME)
//@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
@Slf4j
public class WSApiService{

    @WebMethod
    public Response executeMessage(@WebParam(name="request")Response request) throws ClientException {
        try {
            Response response = new DefaultResponse();

            return response;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        } finally {
            // ws的回复并不会走responseAdvice故在此处进行ThreadLocal的清空
            ThreadLocalContext.remove();
        }
    }

}
