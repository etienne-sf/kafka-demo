package org.etienne.kafka_demo.web;

import org.etienne.kafka_demo.CpuProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/version")
public class ApiVersionController {

	private static Logger LOGGER = LoggerFactory.getLogger(ApiVersionController.class);

	@Autowired
	CpuProducer cpuProducer;

	@PostMapping
	public void setVersion(@RequestParam String version) {
		if (!"v1".equals(version) && !"v2".equals(version)) {
			throw new IllegalArgumentException("Version non valide : " + version);
		}

		LOGGER.info("Version sélectionnée : {}", version);
		cpuProducer.setVersion(CpuProducer.VersionMessageCpu.valueOf(version));
	}
}
