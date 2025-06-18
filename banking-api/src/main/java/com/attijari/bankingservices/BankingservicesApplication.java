package com.attijari.bankingservices;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingservicesApplication.class, args);
	}
	private Connector connector(){
		Connector connector = new Connector (TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setPort(8080);
		connector.setSecure (false);
		connector.setScheme("http");
		return connector;
	}
	@Bean
	public ServletWebServerFactory servletWebServletFactory(){
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addAdditionalTomcatConnectors (connector());
		return factory;
	}
}
