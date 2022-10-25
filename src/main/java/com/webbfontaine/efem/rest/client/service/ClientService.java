package com.webbfontaine.efem.rest.client.service;

import com.webbfontaine.efem.rest.client.Host;

public interface ClientService<Req, Res> {

    Res process(Req req, Host host);

    String getTarget();
}
