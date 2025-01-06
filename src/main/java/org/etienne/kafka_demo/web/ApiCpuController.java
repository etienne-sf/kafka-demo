package org.etienne.kafka_demo.web;

import java.util.HashMap;
import java.util.Map;

import org.etienne.kafka_demo.CpuUsage1;
import org.etienne.kafka_demo.CpuUsage2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ApiCpuController {

	@Autowired
	private KafkaCpuConsumer kafkaCpuConsumer;

	@GetMapping("/api/cpu-usage")
	public Object getCpuData() {
		Map<String, Object> map = new HashMap<>();
		Object o = kafkaCpuConsumer.getDerniereMesureRecu();

		if (o != null) {
			if (o instanceof CpuUsage1) {
				map.put("cpu", ((CpuUsage1) o).getCpu());
			} else if (o instanceof CpuUsage2) {
				map.put("cpu", ((CpuUsage2) o).getCpu());
				map.put("user", ((CpuUsage2) o).getUser());
				map.put("sys", ((CpuUsage2) o).getSys());
			} else {
				log.error("Type d'objet inconnu : {}", o.getClass().getName());
			}
		}

		return map;
	}
}
