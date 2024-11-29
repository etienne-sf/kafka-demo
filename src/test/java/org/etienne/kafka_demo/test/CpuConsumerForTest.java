/**
 * 
 */
package org.etienne.kafka_demo.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.etienne.kafka_demo.CpuProducer;
import org.etienne.kafka_demo.CpuUsage;
import org.etienne.kafka_demo.web.KafkaCpuConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Cette classe étend le vrai {@link CpuProducer}, pour lui ajouter des capacités nécessaires aux tests automatisés
 * (gestion du Latch notamment)
 * 
 * @author etienne-sf
 */
@Service
public class CpuConsumerForTest {

	@Autowired
	KafkaCpuConsumer cpuConsumer;

	private CountDownLatch latch = new CountDownLatch(1);

	CpuUsage lastReceivedMessage = null;

	@KafkaListener(topics = "${kafka.topic.monitoring.cpu}", groupId = "${kafka.consumer.group.monitoring.cpu}")
	public void consume(/*
						 * @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key, //
						 * 
						 * @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, //
						 */
			CpuUsage cpuUsage) {
		cpuConsumer.consume(cpuUsage);
		lastReceivedMessage = cpuUsage;
		latch.countDown();
	}

	public void resetLatch() {
		latch = new CountDownLatch(1);
		lastReceivedMessage = null;
	}

	public boolean await(int duration, TimeUnit unit) throws InterruptedException {
		return latch.await(duration, unit);
	}

	public CpuUsage getLastReceivedMessage() {
		return lastReceivedMessage;
	}

}
