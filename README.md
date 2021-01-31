# Flink Twitter Streaming

This is a little project that I've used to learn a bit of Apache Flink.

## What it uses

- Apache Flink
- Flink Twitter connector
- Twitter API (v1 and v2)

## How to run the application

- Add an `application.properties` file with the following keys (place in `src/main/resources`):

    ```properties
    api.key=<YOUR API KEY FROM TWITTER>
    api.token=<YOUR API TOKEN FROM TWITTER>
    api.secret.key=<YOUR API SECRET KEY FROM TWITTER>
    api.secret.token=<YOUR API SECRET TOKEN FROM TWITTER>
    ```
  
    These keys will require you to create a Twitter developer application (kind of annoying to do) and from there you should be able to access these keys from your application's settings page.

- In Intellij you can go to `Application.java` and run the `main` function. This is by far the easiest why to run it.

    - The output of the application can be seen in the logs/console.
  
- Run within a Flink standalone cluster:
  
    - Build the application, by running the command:
        
        ```shell
        ./gradlew assemble
        ```
      
    - Download Flink standalone application, which can be found [here](https://www.apache.org/dyn/closer.lua/flink/flink-1.12.1/flink-1.12.1-bin-scala_2.11.tgz).
    
    - Start the Flink cluster:
    
        ```shell
        .<flink-directory>/bin/start-cluster.sh
        ```
      
        This will allow you to access the cluster's web interface at `localhost:8081`. 
    
    - Submit the application's jar that was built earlier as a job:
    
        ```shell
        .<flink-directory>/bin/flink run <application-directory>/build/libs/flink-twitter-streaming-1.0-SNAPSHOT.jar
        ```
      
    - View the logs and output of the job by viewing the Task Manager on the web interface.
    
        - The logs show the logs.
        - The stdout shows the output of the Flink job as the application is printing its results.
    
    - Stop the cluster:

        ```shell
        .<flink-directory>/bin/stop-cluster.sh
        ```
        