# Introduction 
Le traitement de données massives est devenu une préoccupation majeure dans le domaine de l'informatique, en particulier avec l'explosion de la quantité de données générées quotidiennement dans divers secteurs. Les technologies de traitement distribué, telles que Apache Spark, ont émergé comme des solutions efficaces pour traiter et analyser ces vastes ensembles de données de manière rapide et évolutive.
Ce rapport se concentre sur le développement d'une application Spark en Java dédiée au traitement de données au format CSV. Les données utilisées dans ce contexte représentent des informations sur les achats en ligne, comprenant des détails tels que le timestamp, l'identifiant du client, l'identifiant du produit et le montant de la transaction. Egalement, on va  développer une application Spark utilisant PySpark (Python) pour extraire des informations cruciales à partir de ces données. L'utilisation de Spark SQL à travers les APIs DataFrame facilitera le processus d'analyse en permettant des requêtes avancées sur les données.

# 
## Les Bases de données crées : 
- ***CLIENTS***
  
![Nom de l'image](/captures/CLIENTS.PNG)

- ***COMMANDE***

![Nom de l'image](/captures/Commandes.PNG)

## Démmarer PySpark 
On démarre pySpark par la commande suivante : 
``
pyspark --jars "/home/bouzyan/mysql-connector-java-8.0.13.jar"
``

![Nom de l'image](/captures/démarrer_pyspark.PNG)

1. ***Afficher le nombre total de commandes passées par tous les clients***

![Nom de l'image](/captures/df1.PNG)

2. ***Afficher le client qui a dépensé le plus (en terme de montant)***

![Nom de l'image](/captures/df2.PNG)

![Nom de l'image](/captures/resDF1.PNG)

3. ***Afficher la moyenne des dépenses par client***

![Nom de l'image](/captures/df3.PNG)

![Nom de l'image](/captures/resDF3.PNG)

Après j'ai ajouté une autre commande pour le client 1 pour voir est-ce-que le résultat de la 3ème commande change ou non : 

![Nom de l'image](/captures/ajouterCo.PNG)

Et voici le résultat obtenu:

![Nom de l'image](/captures/res.PNG)

# Traitement de données CSV

1. Télécharger  l'image docker bitnami

   ``
   docker pull bitnami/spark
   ``
   
![Nom de l'image](/captures/bitnami.PNG)

2. Générer les container qui existe dans le fichier docker compose :

``
docker compose -d --build
``

```docker
version: '3'
services:
  spark-master:
    image: bitnami/spark:latest
    container_name: spark-master
    environment:
      - SPARK_MODE=master
      - SPARK_MASTER_PORT=7077
      - SPARK_MASTER_WEBUI_PORT=8080
      - SPARK_DAEMON_MEMORY=2g 
    ports:
      - "7077:7077"
      - "8080:8080"
    volumes:
      - ./volumes/spark-master:/bitnami
    networks:
      - spark-network
  spark-worker-1:
    image: bitnami/spark:latest
    container_name: spark-worker-1
    environment:
      - SPARK_MODE=worker
      - SPARK_MASTER_URL=spark://spark-master:7077
      - SPARK_WORKER_MEMORY=2g  
      - SPARK_WORKER_WEBUI_PORT=4040
    depends_on:
      - spark-master
    volumes:
      - ./volumes/spark-worker-1:/bitnami
    ports:
      - "4040:4040"  
    networks:
      - spark-network
  spark-worker-2:
    image: bitnami/spark:latest
    container_name: spark-worker-2
    environment:
      - SPARK_MODE=worker
      - SPARK_MASTER_URL=spark://spark-master:7077
      - SPARK_WORKER_MEMORY=2g 
      - SPARK_WORKER_WEBUI_PORT=4040
    depends_on:
      - spark-master
    volumes:
      - ./volumes/spark-worker-2:/bitnami
    ports:
      - "4140:4040"  
    networks:
      - spark-network
networks:
  spark-network:
    driver: bridge
```

![Nom de l'image](/captures/starts.PNG)

![Nom de l'image](/captures/dockerSpark.PNG)


Si on accède à ce lien [http://localhost:8080/](http://localhost:8080/),  Voilà ce qu'on va trouver :

![Nom de l'image](/captures/spark1.PNG)

On génére le jar avec la commande : 

``
mvn clean package -DskipTests
``
Ce fichier jar généré on va l'ajouter dans le dossier  **spark-master**

En suite, On ajoute le fichier achats_en_ligne.csv au dossier **spark-master** ainsi au **spark-worker-1** et **spark-worker-2**

Après on exécute la commande suivante :

``
docker exec -it spark-master spark-submit --class org.example.App2 /bitnami/Spark_Docker-1.0-SNAPSHOT.jar
``

![Nom de l'image](/captures/cccc.PNG)

Et voilà ce qu'on va trouvé si on a accèdé au meme lien précèdent: 

![Nom de l'image](/captures/spark2.PNG)

3. ***Afficher le produit le plus vendu en terme de montant total***

```java
 Dataset<Row> totalAmountByProduct = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"))
                .orderBy(desc("total_amount"))
                .limit(1);
        totalAmountByProduct.show();
```
![Nom de l'image](/captures/111.PNG)

4. ***Afficher les 3 produits les plus vendus dans l'ensemble des données***
   
```java
 Dataset<Row> top3Products = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"))
                .orderBy(desc("total_amount"))
                .limit(3);
        top3Products.show();
```
![Nom de l'image](/captures/22.PNG)

5. ***Afficher le montant total des achats pour chaque produit***

```java
 Dataset<Row> totalAmountPerProduct = df.groupBy("produit_id")
                .agg(sum("montant").as("total_amount"));
        totalAmountPerProduct.show();
```

![Nom de l'image](/captures/33.PNG)






