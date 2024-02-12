package com.br.cluster.management;

public interface OnElectionCallback {

    void onElectedToBeLeader();
    void onWorker();
}
