Cluster coordination with zookeeper

RUN

start zookeeeper server at port 2181 with three namespaces: coordinator_service_registry, election, workers_service_registry

     zkServer.sh start
    
Start as many instances as you want of the Application in a separate shell:

    java -jar target/TaskCoordinator-1.0-SNAPSHOT-jar-with-dependencies.jar <port>
    java -jar target/TaskCoordinator-1.0-SNAPSHOT-jar-with-dependencies.jar <port>
    java -jar target/TaskCoordinator-1.0-SNAPSHOT-jar-with-dependencies.jar <port>
    ...

Call the leader

    curl -v -X POST http://127.0.1.1:<port>/search

Call any node

    curl -v -X GET http://127.0.1.1:<port>/status

