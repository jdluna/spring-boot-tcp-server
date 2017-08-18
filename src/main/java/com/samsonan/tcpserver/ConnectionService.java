package com.samsonan.tcpserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.connection.TcpServerConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {

	@Autowired
	public ConnectionService(TcpServerConnectionFactory factory) {
	}
	
}
