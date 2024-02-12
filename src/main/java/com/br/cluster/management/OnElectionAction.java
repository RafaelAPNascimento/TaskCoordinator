package com.br.cluster.management;

import com.br.networking.*;
import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnElectionAction implements OnElectionCallback {

    private final int port;
    private final ServiceRegistry workersServiceRegistry;
    private final ServiceRegistry coordinatorServiceRegistry;

    private WebServer webServer;

    public OnElectionAction(int port, ServiceRegistry workersServiceRegistry, ServiceRegistry coordinatorServiceRegistry) {
        this.port = port;
        this.workersServiceRegistry = workersServiceRegistry;
        this.coordinatorServiceRegistry = coordinatorServiceRegistry;
    }

    @Override
    public void onElectedToBeLeader() {
        workersServiceRegistry.unregisterFromCluster();
        workersServiceRegistry.registerForUpdates();

        if (webServer != null)
            webServer.stop();

        try {
            OnRequestCallback coordinator = new Coordinator(new WebClient(), workersServiceRegistry);
            String address = String.format("http://%s:%d%s", InetAddress.getLocalHost().getHostAddress(), port, coordinator.getEndpoint());
            coordinatorServiceRegistry.registerToCluster(address);

            webServer = new WebServer(port, coordinator);
            webServer.startServer();
        }
        catch (UnknownHostException | KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onWorker() {
        try {
            OnRequestCallback worker = new Worker();
            String address = String.format("http://%s:%d%s", InetAddress.getLocalHost().getHostAddress(), port, worker.getEndpoint());
            workersServiceRegistry.registerToCluster(address);

            webServer = new WebServer(port, worker);
            webServer.startServer();
        }
        catch (UnknownHostException | InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
