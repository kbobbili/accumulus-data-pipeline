package com.accumulus.service;

import static org.apache.spark.sql.functions.array_contains;
import static org.apache.spark.sql.functions.avg;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.count;
import static org.apache.spark.sql.functions.countDistinct;
import static org.apache.spark.sql.functions.desc;
import static org.apache.spark.sql.functions.explode;
import static org.apache.spark.sql.functions.size;

import com.accumulus.data.CustomerToppings;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerToppingsSparkService {

  private SparkSession sparkSession;

  public Dataset<Row> getDataSetFromAPI(String inputAPIUrl) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<List<CustomerToppings>> response =
        restTemplate.exchange(
            inputAPIUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            });

    List<CustomerToppings> customerToppings = response.getBody();
    StructType schema = DataTypes.createStructType(new StructField[]{
        DataTypes.createStructField("email", DataTypes.StringType, true),
        DataTypes.createStructField("toppings", DataTypes.createArrayType(DataTypes.StringType),
            true),
    });
    List<Row> rows = new ArrayList<>();
    for (CustomerToppings customerTopping : customerToppings) {
      rows.add(RowFactory.create(customerTopping.getEmail(), customerTopping.getToppings()));
    }
    Dataset<Row> df = getSparkSession().createDataFrame(rows, schema);
    return df;
  }

  public void totalCountPerTopping(Dataset<Row> df) {
    Dataset<Row> toppingsDf = df.select(explode(col("toppings")).as("topping"));
    Dataset<Row> toppingsCountDf = toppingsDf.groupBy(col("topping"))
        .agg(count(col("topping")).as("total_count"));
    System.out.println("Total count per topping: ");
    toppingsCountDf.show();
    System.out.println("<End>");
    toppingsCountDf.write().format("mongodb").mode("overwrite")
        .option("collection", "total_count_per_topping")
        .save();
  }

  public void uniqueUserCountPerTopping(Dataset<Row> df) {
    Dataset<Row> customerToppingsDf = df
        .select(col("email"), explode(col("toppings")).as("topping"));
    Dataset<Row> uniqueUsersCountDf = customerToppingsDf.groupBy(col("topping"))
        .agg(countDistinct(col("email")).as("unique_user_count"));
    System.out.println("Unique user count per topping: ");
    uniqueUsersCountDf.show();
    System.out.println("<End>");
    uniqueUsersCountDf.write().format("mongodb").mode("overwrite")
        .option("collection", "unique_users_count_per_topping")
        .save();
  }

  public void list3MostPopularToppings(Dataset<Row> df) {
    Dataset<Row> customerToppingsDf = df
        .select(col("email"), explode(col("toppings")).as("topping"));
    Dataset<Row> toppingsCountDf = customerToppingsDf.groupBy(col("topping"))
        .agg(countDistinct(col("email")).as("count"));
    Dataset<Row> mostPopularDf = toppingsCountDf.orderBy(col("count").desc()).limit(3);
    System.out.println("List 3 most popular toppings: ");
    mostPopularDf.show();
    System.out.println("<End>");
    mostPopularDf.write().format("mongodb").mode("overwrite")
        .option("collection", "most_popular_toppings")
        .save();
  }

  public void list3LeastPopularToppings(Dataset<Row> df) {
    Dataset<Row> customerToppingsDf = df
        .select(col("email"), explode(col("toppings")).as("topping"));
    Dataset<Row> toppingsCountDf = customerToppingsDf.groupBy(col("topping"))
        .agg(countDistinct(col("email")).as("count"));
    Dataset<Row> leastPopularDf = toppingsCountDf.orderBy(col("count")).limit(3);
    System.out.println("List 3 least popular toppings ");
    leastPopularDf.show();
    System.out.println("<End>");
    leastPopularDf.write().format("mongodb").mode("overwrite")
        .option("collection", "least_popular_toppings")
        .save();
  }

  public void countNumberOfUsersWhoLikeToHaveMoreThan2Toppings(Dataset<Row> df) {
    Dataset<Row> usersWithMoreThan2ToppingsDf = df.filter(size(col("toppings")).gt(2));
    usersWithMoreThan2ToppingsDf.write().format("mongodb").mode("overwrite")
        .option("collection", "users_with_more_than_2_toppings")
        .save();
    System.out.println(
        "Number of users with more than 2 toppings: " + usersWithMoreThan2ToppingsDf.count());
  }

  public void percentageOfUsersWhoLikeToHaveCheeseWithMushrooms(Dataset<Row> df) {
    long totalUsers = df.count();
    Dataset<Row> usersWhoLikeCheeseAndMushroomToppings = df
        .filter(array_contains(col("toppings"), "a")
            .and(array_contains(col("toppings"), "c")));
    double percentage = (double) usersWhoLikeCheeseAndMushroomToppings.count() / totalUsers * 100;
    System.out.println("Percentage of users who like to have cheese with mushrooms: " + percentage);
  }

  public void averageNumberOfToppingsThatUsersLike(Dataset<Row> df) {
    Dataset<Row> avgToppingsDf = df
        .select(avg(size(col("toppings"))).alias("avg_number_of_toppings"));
    avgToppingsDf.write().format("mongodb").mode("overwrite")
        .option("collection", "avg_number_of_toppings_that_users_like")
        .save();
    System.out.println(
        "Average number of toppings that our customers like: " + avgToppingsDf.first()
            .getDouble(0));
  }

  public void findOutWhichToppingsUsuallyGoTogetherInPairsOf2(Dataset<Row> df) {
    Dataset<Row> customerToppingsDf = df
        .select(col("email"), explode(col("toppings")).alias("topping"));
    Dataset<Row> toppingPairsDf = customerToppingsDf.as("a")
        .join(customerToppingsDf.as("b"), col("a.email").equalTo(col("b.email")))
        .where(col("a.topping").lt(col("b.topping")))
        .select(col("a.topping").alias("topping1"), col("b.topping").alias("topping2"))
        .groupBy(col("topping1"), col("topping2"))
        .count()
        .orderBy(desc("count"))
        .limit(3);
    System.out.println("Top 3 topping pairs that usually go together: ");
    toppingPairsDf.show();
    System.out.println("<End>");
    toppingPairsDf.write().format("mongodb").mode("overwrite")
        .option("collection", "topping_pairs_that_go_together")
        .save();
  }

  public void stop() {
    getSparkSession().stop();
  }

}
