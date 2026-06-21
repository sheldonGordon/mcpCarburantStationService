package fr.chatelain.mcp.carburantstationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarburantStationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarburantStationServiceApplication.class, args);
	}

}
