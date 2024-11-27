package org.etienne.kafka_demo.web;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaCpuConsumer {

	private final CopyOnWriteArrayList<Double> cpuData = new CopyOnWriteArrayList<>();

	private final int MAX_NB_VALEURS = 60; // 1 point par seconde. 60 = 1 minute

	public CopyOnWriteArrayList<Double> getCpuData() {
		return cpuData;
	}

	@KafkaListener(topics = "${kafka.topic.monitoring.cpu}", groupId = "${kafka.consumer.group.monitoring.cpu}")
	public void consume(String message) {
		try {
			double value = Double.parseDouble(message.trim()) * 100;
			if (cpuData.size() >= MAX_NB_VALEURS) { // Limite les données à MAX_NB_VALEURS points
				cpuData.remove(0);
			}
			cpuData.add(value);
		} catch (NumberFormatException e) {
			System.err.println("Invalid message: " + message);
		}
	}
}
