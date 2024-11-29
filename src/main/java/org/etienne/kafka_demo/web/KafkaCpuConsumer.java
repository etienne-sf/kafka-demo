package org.etienne.kafka_demo.web;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.avro.Schema;
import org.etienne.kafka_demo.CpuUsage;
import org.etienne.kafka_demo.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import jakarta.annotation.PostConstruct;

//@Service
public class KafkaCpuConsumer {
	private static Logger LOGGER = LoggerFactory.getLogger(KafkaCpuConsumer.class);
	private final int MAX_NB_VALEURS = 60; // 1 point par seconde. 60 = 1 minute

	@Value("${kafka.consumer.group.monitoring.cpu}")
	String nomTopic;

	@Autowired
	Util util;

	@Autowired
	Map<String, Schema> schemasMonitoringCpu;

	private final CopyOnWriteArrayList<Double> cpuData = new CopyOnWriteArrayList<>();

	@PostConstruct
	void postConstruct() {
		// Lecture du schéma
	}

	@KafkaListener(topics = "${kafka.topic.monitoring.cpu}", groupId = "${kafka.consumer.group.monitoring.cpu}", containerFactory = "kafkaListenerContainerFactory")
	public void consume(/*
						 * @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key, //
						 * 
						 * @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, //
						 */
			CpuUsage cpuUsage) {

		// Record dispose des méta données suivantes : partition, offset, timestamp, key
		// Le message est dans "value"

		LOGGER.trace("Valeur reçue sur le topic {} : cpu={}", nomTopic, cpuUsage.getCpu());

		double value = cpuUsage.getCpu() * 100;
		if (cpuData.size() >= MAX_NB_VALEURS) { // Limite les données à MAX_NB_VALEURS points
			cpuData.remove(0);
		}
		cpuData.add(value);
	}

	/**
	 * Lecture du tableau des dernières mesures reçues sur Kafka. La taille du tableau est déterminée par
	 * {@link #MAX_NB_VALEURS}
	 * 
	 * @return
	 */
	public CopyOnWriteArrayList<Double> getCpuData() {
		return cpuData;
	}
}
