package org.etienne.kafka_demo.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiCpuController {

	@Autowired
	private KafkaCpuConsumer kafkaCpuConsumer;

	@GetMapping("/api/cpu-data")
	public List<Double> getCpuData() {
		return kafkaCpuConsumer.getCpuData();
	}
}
