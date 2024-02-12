package com.br.networking;

import com.br.application.SerializationUtils;
import com.br.cluster.management.ServiceRegistry;
import com.br.model.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Coordinator implements OnRequestCallback {

    private static final String ENDPOINT = "/search";
    private final WebClient webClient;
    private final ServiceRegistry workersServiceRegistry;

    public Coordinator(WebClient webClient, ServiceRegistry workersServiceRegistry) {
        this.webClient = webClient;
        this.workersServiceRegistry = workersServiceRegistry;
    }

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }

    @Override
    public byte[] handleRequest(byte[] request) {
        try {
            List<String> addresses = workersServiceRegistry.getAllServicesAddresses();

            if (addresses.isEmpty()) {
                System.out.println("No workers available");
                return "no workers".getBytes();
            }
            System.out.println("There are " + addresses.size() + " workers available");

            List<CompletableFuture<Result>> futures = new ArrayList<>(addresses.size());

            for (String address : addresses) {
                Result result = new Result();
                result.setLeaderTimeStamp(System.currentTimeMillis());
                byte[] req = SerializationUtils.serialize(result);
                CompletableFuture<Result> future = webClient.sendTask(address, req);
                futures.add(future);
            }

            StringBuilder resp = new StringBuilder();
            for (CompletableFuture<Result> future : futures) {
                Result result = future.get();
                resp.append(result.toString() + "\n");
            }

            return resp.toString().getBytes();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
