package org.etienne.kafka_demo.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.etienne.kafka_demo.CpuProducer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9992", "port=9992" })
class EmbeddedKafkaIntegrationTest {

	@Autowired
	private CpuProducer cpuProducer;
	@Autowired
	private CpuConsumerForTest cpuConsumer;

	@BeforeEach
	void setup() {
		cpuConsumer.resetLatch();
	}

	// @Test ==> Ne fonctionne pas
	public void testProductionEtConsommationMessageCpu() throws Exception {

		cpuProducer.produitUnMessage();

		boolean messageConsumed = cpuConsumer.await(10, TimeUnit.SECONDS);
		assertTrue(messageConsumed);
		assertNotNull(cpuConsumer.getLastReceivedMessage());
		assertNotNull(cpuConsumer.getLastReceivedMessage().getCpu());
	}

}