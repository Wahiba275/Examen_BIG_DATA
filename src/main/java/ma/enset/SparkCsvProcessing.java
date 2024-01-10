package ma.enset;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.*;

public class SparkCsvProcessing {
    public static void main(String[] args) {
        SparkSession ss = SparkSession.builder().appName("test mysql").master("spark://spark-master:7077").getOrCreate();
        Dataset<Row> df=ss.read().option("header",true).option("inferSchema",true).csv("/bitnami/achats_en_ligne.csv");
        df.printSchema();

        // Afficher le produit le plus vendu en termes de montant total
        Dataset<Row> totalAmountByProduct = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"))
                .orderBy(desc("total_amount"))
                .limit(1);
        totalAmountByProduct.show();

        // Afficher les 3 produits les plus vendus dans l'ensemble des données
        Dataset<Row> top3Products = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"))
                .orderBy(desc("total_amount"))
                .limit(3);
        top3Products.show();

        // Afficher le montant total des achats pour chaque produit
        Dataset<Row> totalAmountPerProduct = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"));
        totalAmountPerProduct.show();

        // Arrêter la session Spark
        ss.stop();
    }
}
