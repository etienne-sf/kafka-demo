/**
 * 
 */
package org.etienne.kafka_demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * Classe chargée d'alimentée le topic "monitoring.cpu.01" avec les données mesurées de consommation de CPU. <br/>
 * 
 * @See https://github.com/oshi/oshi
 */
@Component
public class CpuProducer implements CommandLineRunner {

	private static Logger LOGGER = LoggerFactory.getLogger(CpuProducer.class);

	@Value("${cpu.delai.mesure}")
	long delaiEntreDeuxMesures;

	@Value("${kafka.topic.monitoring.cpu}")
	String topicName;

	@Autowired
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Override
	public void run(String... args) throws Exception {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		CentralProcessor cpu = hal.getProcessor();
		double nbTicks;

		while (true) {
			nbTicks = cpu.getSystemCpuLoad(1000); // Usage de la CPU pendant les 1000 ms de délai
			// LOGGER.info("publication de la cpu : nbTicks={}", nbTicks);
			kafkaTemplate.send(topicName, Double.toString(nbTicks));
		}
	}

}
