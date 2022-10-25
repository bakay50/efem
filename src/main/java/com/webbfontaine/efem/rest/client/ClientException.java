package com.webbfontaine.efem.rest.client;

public class ClientException extends Exception {
    private String code;

    public ClientException(String message, String code) {
        super(message);
    }

    public String getCode() {
        return this.code;
    }
}

