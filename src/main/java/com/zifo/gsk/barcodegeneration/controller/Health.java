package com.zifo.gsk.barcodegeneration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * @author zifo
 *
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Health API", description = "Check the health of the API")
public class Health {

	@Value("${host}")
	private String hostName;

	@Operation(summary = "Check the health of the API")
	@GetMapping("/healthCheck")
	public String healthCheck() {
		return String.format("API is up and running for %s server", hostName);
	}
}
