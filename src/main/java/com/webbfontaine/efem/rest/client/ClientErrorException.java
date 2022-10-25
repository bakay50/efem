package com.webbfontaine.efem.rest.client;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ClientErrorException extends WebApplicationException {
    private static final long serialVersionUID = 5248330949928169612L;

    static Response validate(Response response, Response.Status expectedStatus) {
        if(expectedStatus.getStatusCode() != response.getStatus()) {
            throw new IllegalArgumentException(String.format("Invalid response status code. Expected [%d], was [%d].", new Object[]{Integer.valueOf(expectedStatus.getStatusCode()), Integer.valueOf(response.getStatus())}));
        } else {
            return response;
        }
    }

    static Response validate(Response response, Response.Status.Family expectedStatusFamily) {
        if(response.getStatusInfo().getFamily() != expectedStatusFamily) {
            throw new IllegalArgumentException(String.format("Status code of the supplied response [%d] is not from the required status code family \"%s\".", new Object[]{Integer.valueOf(response.getStatus()), expectedStatusFamily}));
        } else {
            return response;
        }
    }

    public ClientErrorException(Response.Status status) {
        super((Throwable)null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, Response.Status status) {
        super(message, (Throwable)null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(int status) {
        super((Throwable)null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, int status) {
        super(message, (Throwable)null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(Response response) {
        super((Throwable)null, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, Response response) {
        super(message, (Throwable)null, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(Response.Status status, Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, Response.Status status, Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(int status, Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, int status, Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(Response response, Throwable cause) {
        super(cause, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    public ClientErrorException(String message, Response response, Throwable cause) {
        super(message, cause, validate(response, Response.Status.Family.CLIENT_ERROR));
    }
}