package com.webbfontaine.efem.tvf

class Host implements com.webbfontaine.efem.rest.client.Host{

    String url
    String path
    String userName
    String password

    public Host(String url, String path, String userName, String password){
        this.url = url
        this.path = path
        this.userName = userName
        this.password = password
    }

    @Override
    String getUrl() {
        return this.url
    }

    void setUrl(String url) {
        this.url = url
    }

    @Override
    String getPath() {
        return this.path
    }

    void setPath(String path) {
        this.path = path
    }

    @Override
    String getUserName() {
        return this.userName
    }

    void setUserName(String userName) {
        this.userName = userName
    }

    @Override
    String getPassword() {
        return this.password
    }

    void setPassword(String password) {
        this.password = password
    }

}
