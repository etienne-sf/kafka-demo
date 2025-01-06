package org.etienne.kafka_demo.web;

import org.etienne.kafka_demo.CpuProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/version")
@Slf4j
public class ApiVersionController {

	@Autowired
	CpuProducer cpuProducer;

	@PostMapping
	public void setVersion(@RequestParam String version) {
		if (!"v1".equals(version) && !"v2".equals(version)) {
			throw new IllegalArgumentException("Version non valide : " + version);
		}

		log.info("Version sélectionnée : {}", version);
		cpuProducer.setVersion(CpuProducer.VersionMessageCpu.valueOf(version));
	}
}
