package com.kalafche;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Web Configuration expose the all services
 * 
 * @author malalanayake
 * 
 */
@Configuration
@EnableWebMvc
@EnableScheduling
public class WebConfig extends WebMvcConfigurerAdapter {

	public WebConfig() {
		super();
	}

}
