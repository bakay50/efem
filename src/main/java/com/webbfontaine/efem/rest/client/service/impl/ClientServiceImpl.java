package com.webbfontaine.efem.rest.client.service.impl;

import com.webbfontaine.efem.rest.client.Host;
import com.webbfontaine.efem.rest.client.service.ClientService;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

abstract class ClientServiceImpl<Req, Res> implements ClientService<Req, Res> {
    private static Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);
    public ClientServiceImpl(){
    }

    private ClientConfig clientConfig;

    final void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public String getTarget() {
        return null;
    }

    protected abstract WebTarget getWebTarget(Host host, Client client);


    protected Client getClient(Host host) {
        Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
        return client;
    }

    protected abstract Res processResponse(Response res);

    protected abstract Invocation buildInvocation(WebTarget webTarget, Req req);

    public Res process(Req req, Host host) {
        Client client = getClient(host);
        WebTarget webTarget = getWebTarget(host, client);
        LOGGER.debug("Executing request to {}", webTarget);

        Invocation invocation = buildInvocation(webTarget, req);
        Response response = invocation.invoke();
        buildException(response);
        Response.StatusType statusInfo = response.getStatusInfo();
        LOGGER.debug("response status info: {}", statusInfo);
        Response.Status status = Response.Status.fromStatusCode(statusInfo.getStatusCode());
        LOGGER.debug("response status: {}", status);
        Object out = null;
        if(status == Response.Status.OK) {
            out = this.processResponse(response);
            return (Res)out;
        } else {
            throw new WebApplicationException("Response is empty");
        }
    }

    protected void buildException(Response response) {
        Response.StatusType statusInfo = response.getStatusInfo();
        Response.Status.Family family = statusInfo.getFamily();
        Response.Status status = Response.Status.fromStatusCode(statusInfo.getStatusCode());
        LOGGER.debug("response status in buildException: {}", status);
        LOGGER.debug("family.ordinal(): {}", family.ordinal());
        switch (family.ordinal()) {
            case 1:
            case 2:
                return;
            case 3:
                throw new RedirectionException(response);
            case 4:
                switch (status.ordinal()) {
                    case 1:
                        throw new NotAuthorizedException(response);
                    case 2:
                        throw new ForbiddenException(response);
                    case 3:
                        throw new NotFoundException(response);
                    case 4:
                        throw new NotAllowedException(response);
                    default:
                        throw new ClientErrorException("Error while executing request", response);
                }
            case 5:
                switch (status.ordinal()) {
                    case 5:
                        throw new InternalServerErrorException(response);
                    case 6:
                        throw new ServiceUnavailableException(response);
                    default:
                        throw new ServerErrorException("Error while executing request", response);

                }
            default:
                throw new ServerErrorException("Error while executing request", response);

        }
    }
}
