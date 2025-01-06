# kafka-demo


# Pré-requis

## Kakfa

Voir le [HowTo](HOWTO.md).

## oshi-core

Cet outil est chargé dans les dépendances du pom.

com.github.oshi:oshi-core permet de lire la CPU et plein d'autres variables de fonctionnement de la machine. C'est utilisé en remplacement de l'outil standard fourni avec le JDK, qui ne fonctionne pas sous Windows.

# Fonctionnement

Le code java est en deux parties :
* Kafka
    * La classe `org.etienne.kafka_demo.CpuProducer` produit les données de suivi de la CPU, et les postes dans un topic Kafka
* Web
    * Le controleur `org.etienne.kafka_demo.web.ApiCpuController` expose une API qui permet à la page web de récupérer les données de CPU à afficher sur la courbe
    * Le controleur `org.etienne.kafka_demo.CpuProducer.ApiVersionController` permet à la page web de choisir la version des messages (v1 ou v2, c'est à dire avec ou sans le données "user CPU" et "System CPU")
    * La classe `org.etienne.kafka_demo.web.KafkaCpuConsumer` lit les données de CPU sur le topic Kafka

La page web est consultable à cette url : [http://localhost:8080/](http://localhost:8080/)
