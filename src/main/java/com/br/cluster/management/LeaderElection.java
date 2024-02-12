package com.br.cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;

import static com.br.application.Application.ELECTION_NAMESPACE;

public class LeaderElection implements Watcher {

    private final ZooKeeper zooKeeper;
    private final OnElectionCallback onElectionCallback;

    private String znode;

    public LeaderElection(ZooKeeper zooKeeper, OnElectionCallback onElectionCallback) {
        this.zooKeeper = zooKeeper;
        this.onElectionCallback = onElectionCallback;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case NodeDeleted:
                try {
                    electLeader();
                } catch (InterruptedException | KeeperException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        znode = zooKeeper.create(ELECTION_NAMESPACE + "/", new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        znode = znode.replace(ELECTION_NAMESPACE + "/", "");
    }

    public void electLeader() throws InterruptedException, KeeperException {
        Stat stat = null;
        String prevZnode = null;
        List<String> znodes = zooKeeper.getChildren(ELECTION_NAMESPACE, this);
        Collections.sort(znodes);
        String leader = znodes.get(0);
        while (stat == null) {
            if (this.znode.equals(leader)) {
                System.out.println("Znode name " + this.znode);
                System.out.println("I am the leader!");
                onElectionCallback.onElectedToBeLeader();
                return;
            }
            else {
                int prev = Collections.binarySearch(znodes, znode) - 1;
                prevZnode = znodes.get(prev);
                stat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + prevZnode, this);
            }
        }
        System.out.println("I am not the leader. The leader is " + leader);
        System.out.println("Watching previous znode " + prevZnode);
        onElectionCallback.onWorker();
    }
}
