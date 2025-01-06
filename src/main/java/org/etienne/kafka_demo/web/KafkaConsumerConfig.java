package org.etienne.kafka_demo.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.etienne.kafka_demo.CpuUsage1;
import org.etienne.kafka_demo.utils.AvroDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${kafka.consumer.group.monitoring.cpu}")
	String consumerGroup;

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Adresse Kafka
		// props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup); // Défini dans
		// l'annotation @KafkaListener
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
		return props;
	}

	@Bean
	public ConsumerFactory<String, CpuUsage1> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
				new AvroDeserializer<CpuUsage1>(CpuUsage1.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, CpuUsage1> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, CpuUsage1> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public KafkaCpuConsumer receiver() {
		return new KafkaCpuConsumer();
	}
}
