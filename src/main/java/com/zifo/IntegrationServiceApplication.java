package com.zifo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zifo
 *
 */
@SpringBootApplication
@ComponentScan({"com.zifo"})
public class IntegrationServiceApplication {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		SpringApplication.run(IntegrationServiceApplication.class, args);
	}
}
