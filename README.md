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


![K_230501_004446 (1)](https://user-images.githubusercontent.com/47704286/235426447-d78d4369-fb87-42d6-b1a5-154056a53711.jpg)

![Screenshot 2023-05-01 003802](https://user-images.githubusercontent.com/47704286/235426505-4ffcdfca-b6d4-4f99-9b27-5407ed945fd0.jpg)

![Screenshot 2023-05-01 004129](https://user-images.githubusercontent.com/47704286/235426531-5d75254f-e4c7-442e-8269-dba0e2add686.jpg)
