package org.etienne.kafka_demo.web;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.etienne.kafka_demo.CpuProducer;
import org.etienne.kafka_demo.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;

import jakarta.annotation.PostConstruct;

//@Service
public class KafkaCpuConsumer {

	private static Logger LOGGER = LoggerFactory.getLogger(KafkaCpuConsumer.class);

	@Value("${kafka.consumer.group.monitoring.cpu}")
	String nomTopic;

	@Autowired
	Util util;

	@Autowired
	Map<String, Schema> schemasMonitoringCpu;

	/**
	 * Dernier message reçu sur Kafka. Ce message sera récupéré par la page web pour
	 * affichage
	 */
	private Object derniereMesureRecu;

	@PostConstruct
	void postConstruct() {
		// Lecture du schéma
	}

	@KafkaListener(topics = "${kafka.topic.monitoring.cpu}", groupId = "${kafka.consumer.group.monitoring.cpu}", containerFactory = "kafkaListenerContainerFactory")
	public void consume(//
			@Header(CpuProducer.HEADER_VERSION) String version, //
			ConsumerRecord<String, Object> message) {

		derniereMesureRecu = message.value();
		LOGGER.trace("Valeur de type {} reçue sur le topic {}", message.getClass().getSimpleName(), nomTopic);
	}

	/**
	 * Lecture du tableau des dernières mesures reçues sur Kafka. La taille du
	 * tableau est déterminée par {@link #MAX_NB_VALEURS}
	 * 
	 * @return
	 */
	public Object getDerniereMesureRecu() {
		return derniereMesureRecu;
	}
}
