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
    
## App Screenshots

![app](https://user-images.githubusercontent.com/47704286/235426754-ea880d19-e1a1-4a8d-ae79-b2a5bacc7aea.png)

![Screenshot 2023-05-01 010937](https://user-images.githubusercontent.com/47704286/235427093-8197cd49-9494-4f73-82eb-3f28f1ba2540.jpg)

![Screenshot 2023-05-01 004129](https://user-images.githubusercontent.com/47704286/235426531-5d75254f-e4c7-442e-8269-dba0e2add686.jpg)
