package com.samsonan.tcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.config.EnableIntegration;

// example of XML configuration:
//@ImportResource("META-INF/spring/integration/tcpClientServerDemo-context.xml")
//@EnableIntegration
@SpringBootApplication
public class SpringBootTcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootTcpServerApplication.class, args);
	}
}
