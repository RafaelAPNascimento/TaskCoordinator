package com.br.networking;

import com.br.application.SerializationUtils;
import com.br.model.Result;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WebClient {

    private final HttpClient client;

    public WebClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public CompletableFuture<Result> sendTask(String address, byte[] payload) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
                .uri(URI.create(address))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body)
                .thenApply(responseBody -> (Result) SerializationUtils.deserialize(responseBody));
    }


}