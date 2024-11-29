

# Commandes courantes (windows)

## Configuration

Ajout des commandes shells dans le path

```
xxx\kafka\bin\windows
```

## Première configuration du serveur

```
KAFKA_CLUSTER_ID="$(bin\windows\kafka-storage.bat random-uuid)"
bin\windows\kafka-storage.bat format --standalone -t $KAFKA_CLUSTER_ID -c config\kraft\reconfig-server.properties
```

## Démarrage du serveur

```
bin\windows\kafka-server-start.bat config\kraft\reconfig-server.properties
```


## Création d'un topic

```
kafka-topics.bat --create --topic "monitoring.cpu.01" --bootstrap-server localhost:9092
```

## Visualisation des infos sur un topic

```
kafka-topics.bat  --describe --from --topic "monitoring.cpu.01" --bootstrap-server localhost:9092
```


## Ecriture de données dans un topic

```
kafka-console-producer.bat --topic "monitoring.cpu.01" --bootstrap-server localhost:9092
```

## Lecture de données dans un topic

En lisant le topic depuis le début :

```
kafka-console-consumer.bat --topic "monitoring.cpu.01" --from-beginning --bootstrap-server localhost:9092
```

Ou à partir de ce qui sera ensuite publié :

```
kafka-console-consumer.bat --topic "monitoring.cpu.01" --bootstrap-server localhost:9092
```

