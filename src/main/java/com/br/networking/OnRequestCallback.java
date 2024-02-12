package com.br.networking;

public interface OnRequestCallback {

    String getEndpoint();

    byte[] handleRequest(byte[] request);
}
