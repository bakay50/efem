package com.webbfontaine.efem.rest.client.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.webbfontaine.efem.rest.client.Host;
import com.webbfontaine.efem.rest.client.service.CheckableClientService;
import com.webbfontaine.epayment.ci.shared.bean.Request;
import com.webbfontaine.epayment.ci.shared.bean.Response;
import com.webbfontaine.epayment.ci.shared.bean.Result;
import com.webbfontaine.epayment.ci.shared.bean.common.Error;
import org.grails.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

class TvfClientService<Req extends Request, Res extends Response> extends ClientServiceImpl<Req, Res> implements CheckableClientService {
    private static Logger LOGGER = LoggerFactory.getLogger(TvfClientService.class);



    public boolean check(String val) {
        return false;
    }

    @Override
    protected WebTarget getWebTarget(Host host, Client client) {
        WebTarget webTarget = client.target(host.getUrl());
        if (!host.getPath().isEmpty()) {
            webTarget = webTarget.path(host.getPath());
        }
        return webTarget;
    }

    @Override
    protected Res processResponse(javax.ws.rs.core.Response res) {
        String err = res.getHeaderString("error");
        LOGGER.debug("err: "+err);
        if (err != null) {
            Response r = (Response) res.readEntity(com.webbfontaine.epayment.ci.shared.bean.error.Response.class);
            Iterable errors = Iterables.transform(r.getErrors(), new Function() {

                public Object apply(Object input) {
                    com.webbfontaine.epayment.ci.shared.bean.common.Error e = (Error) input;
                    return e.getCode();
                }
            });
            StringBuilder sb = new StringBuilder(err);
            sb.append(" (");
            Joiner.on(",").appendTo(sb, errors);
            sb.append(")");
            throw new ServerErrorException(sb.toString(), javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        } else {
            final String responseEntity = res.readEntity(String.class);
            LOGGER.debug("Response Entity: "+responseEntity);
            com.webbfontaine.efem.rest.bean.Response response = new com.webbfontaine.efem.rest.bean.Response(){

                public String getId() {
                    return null;
                }

                public String getOperation() {
                    return null;
                }

                public Result getResult() {
                    return null;
                }

                public Iterable<Error> getErrors() {
                    return null;
                }

                public boolean hasErrors() {
                    return false;
                }

                public String getData() {
                    return responseEntity;
                }
            };
            return (Res) response;
        }
    }

    @Override
    protected Invocation buildInvocation(WebTarget webTarget, Req req) {
        LOGGER.debug("Executing request: {}", req);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML_TYPE);
        Cookie[] cookies = getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                invocationBuilder.cookie(cookie.getName(), cookie.getValue());
            }
        }
        Invocation invocation = invocationBuilder.buildGet();
        LOGGER.debug("Return invocation");
        return invocation;
    }

    private Cookie[] getCookies(){
        HttpServletRequest request = WebUtils.retrieveGrailsWebRequest().getCurrentRequest();
        return request.getCookies();
    }
}
