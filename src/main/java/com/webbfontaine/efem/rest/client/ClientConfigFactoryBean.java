package com.webbfontaine.efem.rest.client;

import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import javax.ws.rs.client.ClientRequestFilter;
import java.util.Collection;
import java.util.Iterator;

public class ClientConfigFactoryBean extends AbstractFactoryBean<ClientConfig> {
    private Collection<ClientRequestFilter> clientRequestFilters;

    public ClientConfigFactoryBean() {
    }

    public void setClientRequestFilters(Collection<ClientRequestFilter> clientRequestFilters) {
        this.clientRequestFilters = clientRequestFilters;
    }

    protected ClientConfig createInstance() throws Exception {
        ClientConfig config = new ClientConfig();
        if(this.clientRequestFilters != null) {
            Iterator var2 = this.clientRequestFilters.iterator();

            while(var2.hasNext()) {
                ClientRequestFilter f = (ClientRequestFilter)var2.next();
                config.register(f);
            }
        }

        return config;
    }

    public Class<?> getObjectType() {
        return ClientConfig.class;
    }
}

