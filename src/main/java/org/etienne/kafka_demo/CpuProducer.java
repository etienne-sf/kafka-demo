/**
 * 
 */
package org.etienne.kafka_demo;

import java.time.Instant;
import java.util.Map;

import org.apache.avro.Schema;
import org.etienne.kafka_demo.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * Classe chargée d'alimentée le topic "monitoring.cpu.01" avec les données
 * mesurées de consommation de CPU. <br/>
 * 
 * @See https://github.com/oshi/oshi
 */
@Component
@Slf4j
public class CpuProducer implements CommandLineRunner {

	public final static String HEADER_VERSION = "X-Version";

	public enum VersionMessageCpu {
		v1, v2
	}

	@Value("${cpu.delai.mesure}")
	long delaiEntreDeuxMesures;

	@Value("${kafka.topic.monitoring.cpu}")
	String topicName;

	@Autowired
	KafkaTemplate<String, CpuUsage1> kafkaTemplate;

	@Autowired
	Map<String, Schema> schemasMonitoringCpu;

	@Autowired
	Util util;

	/** La version dans laquelle les messages sont envoyés. v1, au départ */
	VersionMessageCpu version = VersionMessageCpu.v1;

	/** Nombre de fois où le message sur la consommation de CPU doit être envoyé */
	int nbIterations;

	protected CentralProcessor processor;

	/** Constructeur par défaut : boucle sans fin sur l'envoi des messages */
	public CpuProducer() {
		this(-1);
	}

	/**
	 * 
	 * @param nbIterations Nb de messages à envoyer dans le topic Kafka
	 */
	public CpuProducer(int nbIterations) {
		this.nbIterations = nbIterations;
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		processor = hal.getProcessor();
	}

	/**
	 * Setters pour définir le nombre d'itérations qui restent à faire. Un nombre
	 * négatif provoque une boucle infinie.
	 * 
	 * @param nbIterations
	 */
	public void setNbIterations(int nbIterations) {
		this.nbIterations = nbIterations;
	}

	/** Changement à la volée du format des messages envoyés */
	public void setVersion(VersionMessageCpu version) {
		this.version = version;
	}

	@Override
	public void run(String... args) throws Exception {
		Message<?> message = null;
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		// long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();

		while (nbIterations != 0) {

			// Wait a second...
			Thread.sleep(1000);

			long[] ticks = processor.getSystemCpuLoadTicks();
			long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
			long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
			long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
			long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
			long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
			long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
			long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
			long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
			long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

			synchronized (this) {
				// synchronized garantit que la version ne change pas entre le "switch" et la
				// construction du message
				switch (version) {
				case v1:
					CpuUsage1 cpuUsage1 = CpuUsage1.newBuilder()//
							.setTimestamp(Instant.now())//
							.setCpu(processor.getSystemCpuLoadBetweenTicks(prevTicks))//
							.build();
					message = MessageBuilder//
							.withPayload(cpuUsage1)//
							.setHeader(KafkaHeaders.TOPIC, topicName)//
							// .setHeader(KafkaHeaders.MESSAGE_KEY, key)//
							.setHeader(HEADER_VERSION, version.toString().getBytes())//
							.build();
					break;
				case v2:
					CpuUsage2 cpuUsage2 = CpuUsage2.newBuilder()//
							.setTimestamp(Instant.now())//
							.setCpu(processor.getSystemCpuLoadBetweenTicks(prevTicks))//
							.setUser((double) user / totalCpu)//
							.setSys((double) sys / totalCpu)//
							.build();
					message = MessageBuilder//
							.withPayload(cpuUsage2)//
							.setHeader(KafkaHeaders.TOPIC, topicName)//
							// .setHeader(KafkaHeaders.MESSAGE_KEY, key)//
							.setHeader(HEADER_VERSION, version.toString().getBytes())//
							.build();

					break;
				default:
					log.error("Version non gérée, lors de la production d'un message : {}", version);
				}
			}

			kafkaTemplate.send(message);

			// Alternative :
			// ProducerRecord<String, CpuUsage> record = new ProducerRecord<>(topicName,
			// cpuUsage);
			// record.headers().add("X-Version", "v1".getBytes());
			// kafkaTemplate.send(record);

			// Si un nombre max d'itérations a été fourni, on le décrémente
			if (nbIterations > 0) {
				nbIterations -= 1;
			}
			prevTicks = ticks;
		}
	}

}
