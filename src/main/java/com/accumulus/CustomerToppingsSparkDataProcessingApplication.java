package com.accumulus;

import com.accumulus.service.CustomerToppingsSparkService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class CustomerToppingsSparkDataProcessingApplication {

  public static void main(String[] args) throws InterruptedException {
    //This starts a sample standalone mode Apache Spark data processing pipeline
    // that runs on the all of the cores available on the local system.
    SparkSession spark = SparkSession
        .builder()
        .appName("customer_toppings")
        .master("local[*]")
        .config("spark.mongodb.write.connection.uri", "mongodb://accumulus-monogdb:27017/accumulus")
        .config("spark.mongodb.read.connection.uri", "mongodb://accumulus-monogdb:27017/accumulus")
        .getOrCreate();
    CustomerToppingsSparkService customerToppingsSparkService = new CustomerToppingsSparkService(spark);
    //This pipeline reads the data ingress on start up by hitting the below endpoint.
    Dataset<Row> df = customerToppingsSparkService
        .getDataSetFromAPI("http://accumulus-analytics-app:8080/accumulus/customerToppings");
    System.out.println("Calling the customer toppings endpoint to fetch the customer survey data...");
    System.out.println("Customer Toppings Data Ingress: ");
    df.cache();//Caching the dataset so that it can re-used without the need of re-computing it for all the following analysis.
    df.show();
    System.out.println("<End>");
    customerToppingsSparkService.totalCountPerTopping(df);
    customerToppingsSparkService.uniqueUserCountPerTopping(df);
    customerToppingsSparkService.list3MostPopularToppings(df);
    customerToppingsSparkService.list3LeastPopularToppings(df);
    customerToppingsSparkService.countNumberOfUsersWhoLikeToHaveMoreThan2Toppings(df);
    customerToppingsSparkService.percentageOfUsersWhoLikeToHaveCheeseWithMushrooms(df);
    customerToppingsSparkService.averageNumberOfToppingsThatUsersLike(df);
    customerToppingsSparkService.findOutWhichToppingsUsuallyGoTogetherInPairsOf2(df);

    customerToppingsSparkService.stop();
  }

}
