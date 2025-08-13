package com.zifo.gsk.barcodegeneration;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author zifo
 *
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * non-parameterized constructor
	 */
	public ServletInitializer() {
		super();
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(IntegrationServiceApplication.class);
	}

}
