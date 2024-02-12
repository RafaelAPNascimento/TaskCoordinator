package com.br.networking;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {

    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());

    private static final String STATUS_ENDPOINT = "/status";
    private static final int POOL_SIZE = 4;

    private final int port;
    private HttpServer server;
    private final OnRequestCallback onRequestCallback;

    public WebServer(int port, OnRequestCallback onRequestCallback) {
        this.port = port;
        this.onRequestCallback = onRequestCallback;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(onRequestCallback.getEndpoint());

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);

        server.setExecutor(Executors.newFixedThreadPool(POOL_SIZE));
        server.start();
    }

    public void stop() {
        server.stop(5);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {

        LOG.info("handling task request...");
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().flush();
            return;
        }
        byte[] responseBytes = onRequestCallback.handleRequest(exchange.getRequestBody().readAllBytes());
        sendResponse(responseBytes, exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
            outputStream.flush();
        }
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        LOG.info("handling status check...");
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        String response = "Server is alive";
        sendResponse(response.getBytes(), exchange);
    }
}
