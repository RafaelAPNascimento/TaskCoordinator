package com.br.application;

import com.br.cluster.management.LeaderElection;
import com.br.cluster.management.OnElectionAction;
import com.br.cluster.management.OnElectionCallback;
import com.br.cluster.management.ServiceRegistry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {

    private static final String ZOOKEEPER_URL = "localhost:2181";
    private static final int ZOOKEEPER_SESSION = 3000;

    public static final String ELECTION_NAMESPACE = "/election";
    public static final String COORDINATOR_SERVICE_REGISTRY = "/coordinator_service_registry";
    public static final String WORKERS_SERVICE_REGISTRY = "/workers_service_registry";

    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(args[0]);

        Application application = new Application();
        ZooKeeper zooKeeper = application.connectToZookeeper();

        ServiceRegistry workersServiceRegistry = new ServiceRegistry(zooKeeper, WORKERS_SERVICE_REGISTRY);
        ServiceRegistry coordinatorServiceRegistry = new ServiceRegistry(zooKeeper, COORDINATOR_SERVICE_REGISTRY);
        OnElectionCallback onElectionCallback = new OnElectionAction(port, workersServiceRegistry, coordinatorServiceRegistry);
        LeaderElection leaderElection = new LeaderElection(zooKeeper, onElectionCallback);

        leaderElection.volunteerForLeadership();
        leaderElection.electLeader();
        application.start();
        application.stop();
    }

    private ZooKeeper connectToZookeeper() throws IOException {
        zooKeeper = new ZooKeeper(ZOOKEEPER_URL, ZOOKEEPER_SESSION, this);
        return zooKeeper;
    }

    private void start() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    private void stop() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected)
                    System.out.println("Connected to Zookeeper");
                else {
                    System.out.println("Disconnected from Zookeeper");
                    synchronized (zooKeeper) {
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}