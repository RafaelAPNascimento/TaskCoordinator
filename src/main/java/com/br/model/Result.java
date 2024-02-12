package com.br.model;

import java.io.Serializable;

public class Result implements Serializable {

    private long leaderTimeStamp;
    private long workerTimeStamp;
    private String fromZnode;

    public Result() {}

    public Result(long leaderTimeStamp, String fromZnode) {
        this.leaderTimeStamp = leaderTimeStamp;
        this.fromZnode = fromZnode;
    }

    public long getLeaderTimeStamp() {
        return leaderTimeStamp;
    }

    public void setLeaderTimeStamp(long leaderTimeStamp) {
        this.leaderTimeStamp = leaderTimeStamp;
    }

    public String getFromZnode() {
        return fromZnode;
    }

    public void setFromZnode(String fromZnode) {
        this.fromZnode = fromZnode;
    }

    public long getWorkerTimeStamp() {
        return workerTimeStamp;
    }

    public void setWorkerTimeStamp(long workerTimeStamp) {
        this.workerTimeStamp = workerTimeStamp;
    }

    @Override
    public String toString() {
        return "Result {" +
                "leaderTimeStamp: " + leaderTimeStamp +
                ", workerTimeStamp: " + workerTimeStamp +
                ", fromZnode: " + fromZnode + '\'' +
                "}\n\n";
    }
}
