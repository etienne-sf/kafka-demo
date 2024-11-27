package org.etienne.kafka_demo.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Service
public class CpuService {
	public List<Integer> getCpuUsageLastHour() {
		List<Integer> cpuUsage = new ArrayList<>();
		Random random = new Random();

		// Générer des données pour 60 minutes (valeurs aléatoires entre 0 et 100)
		for (int i = 0; i < 60; i++) {
			cpuUsage.add(random.nextInt(101));
		}
		return cpuUsage;
	}
}
