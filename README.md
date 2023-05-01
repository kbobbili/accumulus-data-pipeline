# accumulus-data-pipeline

## Setup


1) Run the maven build of this project. This will build the pipeline as a jar and package it as docker image name `accumulus-data-pipeline`.
    ```
        mvn clean install
    ```

2) Run the maven build of the [web application]() project. This will build the web app and package it as another docker image named `accumulus-analytics-app`. 


3) Now run docker compose to spin up both images along with dependent services. 
    ```
        docker-compose up
    ```
    