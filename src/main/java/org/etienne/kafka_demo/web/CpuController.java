package org.etienne.kafka_demo.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//@Controller
public class CpuController {

	@Autowired
	private CpuService cpuService;

	@GetMapping("/")
	public String showCpuChart(Model model) {
		List<Integer> cpuData = cpuService.getCpuUsageLastHour();
		System.out.println("CPU Data: " + cpuData); // Vérifie les données
		model.addAttribute("cpuData", cpuData);
		return "index";
	}
}
