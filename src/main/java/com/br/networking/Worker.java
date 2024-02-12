package com.br.networking;

import com.br.application.SerializationUtils;
import com.br.model.Result;

public class Worker implements OnRequestCallback {

    private static final String ENPOINT = "/task";

    @Override
    public String getEndpoint() {
        return ENPOINT;
    }

    @Override
    public byte[] handleRequest(byte[] request) {
        System.out.println("Worker received task...");
        Result result = (Result) SerializationUtils.deserialize(request);
        result.setFromZnode("OK");
        result.setWorkerTimeStamp(System.currentTimeMillis());

        return SerializationUtils.serialize(result);
    }
}
