# accumulus-data-pipeline

## Intro

This pipeline app retreives the customer toppings API and processes the data on a standalone mode spark cluster and stores the results in mongo db. 
The [web app](https://github.com/kbobbili/accumulus-analytics-app) provides endpoints to retreive the saved metrics.

## Setup


1) Run the maven build of this project. This will build the pipeline as a jar and package it as docker image name `accumulus-data-pipeline`.
    ```
        mvn clean install
    ```

2) Run the maven build of the [web application](https://github.com/kbobbili/accumulus-analytics-app) project. This will build the web app and package it as another docker image named `accumulus-analytics-app`. 


3) Now run docker compose to spin up both images along with dependent services. 
    ```
        docker-compose up
    ```
    
## Demo Screenshots

<div style="width:60px ; height:60px">
![HLD](https://www.dropbox.com/s/exjn4o6emmooc7d/K_230501_004446%20%281%29.jpg?dl=0)
<div>

