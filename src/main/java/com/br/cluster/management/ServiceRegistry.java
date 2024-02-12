package com.br.cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceRegistry implements Watcher {

    private final ZooKeeper zooKeeper;
    private final String namespace;

    private List<String> allServicesAddresses;
    private String znodeName;

    public ServiceRegistry(ZooKeeper zooKeeper, String namespace) {
        this.zooKeeper = zooKeeper;
        this.namespace = namespace;
    }

    public void registerForUpdates() {
        try {
            updateAddresses();
        } catch (InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterFromCluster() {
        try {
            if (znodeName != null && zooKeeper.exists(namespace + "/" + znodeName, false) != null) {
                zooKeeper.delete(namespace + "/" + znodeName, -1);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerToCluster(String metadata) throws InterruptedException, KeeperException {
        znodeName = zooKeeper.create(namespace + "/", metadata.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        znodeName = znodeName.replace(namespace + "/", "");
    }

    public void updateAddresses() throws InterruptedException, KeeperException {
        List<String> znodes = zooKeeper.getChildren(namespace, this);
        allServicesAddresses = new ArrayList<>(znodes.size());
        for (String znode : znodes) {
            if (znode == null)
                continue;
            Stat stat = zooKeeper.exists(namespace + "/" + znode, false);
            byte[] addr = zooKeeper.getData(namespace + "/" + znode, false, stat);
            String address = new String(addr);
            allServicesAddresses.add(address);
        }
        allServicesAddresses = Collections.unmodifiableList(allServicesAddresses);
        System.out.println(allServicesAddresses);
    }

    public List<String> getAllServicesAddresses() {
        try {
            if (allServicesAddresses == null)
                updateAddresses();
            return allServicesAddresses;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            updateAddresses();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }
    }
}
