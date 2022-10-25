package com.webbfontaine.efem.tvf.impl

import com.webbfontaine.epayment.ci.shared.bean.Request

class RequestImpl implements Request{
    String id;
    String operation;

    RequestImpl(String id, String operation){
        this.id = id;
        this.operation = operation;
    }
    @Override
    public String getId(){
        return this.id
    }

    @Override
    public String getOperation(){
        return this.operation
    }
}
