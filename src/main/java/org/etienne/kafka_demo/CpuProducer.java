/**
 * 
 */
package org.etienne.kafka_demo;

import java.util.Map;

import org.apache.avro.Schema;
import org.etienne.kafka_demo.utils.Util;
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

	// private static Logger LOGGER = LoggerFactory.getLogger(CpuProducer.class);

	@Value("${cpu.delai.mesure}")
	long delaiEntreDeuxMesures;

	@Value("${kafka.topic.monitoring.cpu}")
	String topicName;

	@Autowired
	KafkaTemplate<String, CpuUsage> kafkaTemplate;

	@Autowired
	Map<String, Schema> schemasMonitoringCpu;

	@Autowired
	Util util;

	protected CentralProcessor cpu;

	public CpuProducer() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		cpu = hal.getProcessor();
	}

	@Override
	public void run(String... args) throws Exception {
		while (true) {
			produitUnMessage();

		}
	}

	/**
	 * Cette message isole la production d'un message. Cela permet de l'utilise dans les tests unitaires, pour vérifier
	 * la sérialisation et désérialisation associée
	 */
	public void produitUnMessage() {
		double nbTicks = cpu.getSystemCpuLoad(1000); // Usage de la CPU pendant les 1000 ms de délai

		CpuUsage cpuUsage = CpuUsage.newBuilder()//
				.setCpu(nbTicks)//
				.build();
		kafkaTemplate.send(topicName, cpuUsage);
	}

}
