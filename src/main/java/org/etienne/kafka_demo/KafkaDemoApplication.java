package org.etienne.kafka_demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.etienne.kafka_demo.utils.AvroSerializer;
import org.etienne.kafka_demo.utils.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class KafkaDemoApplication {

	@Value("${kafka.host}")
	String kafkaHost;

	@Value("${kafka.port}")
	int kafkaPort;

	@Value("${kafka.schema.registry.url}")
	String schemaRegistryUrl;

	public static Map<String, Schema> schemasMonitoringCpu = null;

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, CpuUsage1> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, CpuUsage1> kafkaTemplate() {
		return new KafkaTemplate<String, CpuUsage1>(producerFactory());
	}

	/**
	 * Consruction de la {@link Map} des schémas avro pour le topic des CPU. La clé
	 * est la version (v1, v2...). La valeur est l'instance de {@link Schema}
	 * correspondante.
	 * 
	 * @param util
	 * @return
	 */
	@Bean
	public Map<String, Schema> schemasMonitoringCpu(Util util) {
		// Constructionn du singletong accessibles aux sérialiseurs et désérialiseurs en
		// même temps
		schemasMonitoringCpu = new HashMap<>();

		schemasMonitoringCpu.put("v1", util.loadSchema("avro/monitoring.cpu_v1.avsc"));
		schemasMonitoringCpu.put("v2", util.loadSchema("avro/monitoring.cpu_v2.avsc"));

		return schemasMonitoringCpu;
	};

}
