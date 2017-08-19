package com.samsonan.tcpserver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpServerConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

//	@Autowired @Qualifier("outChannel")
	private MessageChannel outChannel;

//	@Autowired @Qualifier("pgAdapter")
	private MessageHandler messageHandler;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired 
	private AbstractServerConnectionFactory connFactory;
		
	@RequestMapping("/connections")
	public String getConnectionsCount() {
		return connFactory.getOpenConnectionIds().size()+"";
	}

	@RequestMapping("/send")
	public void sendMessage() throws Exception {
		Message<String> message = MessageBuilder.withPayload("Message Payload")
                .setHeader("Message_Header1", "Message_Header1_Value")
                .setHeader("Message_Header2", "Message_Header2_Value")
                .build();
		System.out.println("send message: " + message);
		System.out.println("bean:"+context.getBean("outChannel"));
		outChannel = (MessageChannel) context.getBean("outChannel");
//		outChannel.send(message);
		messageHandler = (MessageHandler) context.getBean("pgAdapter");
//		messageHandler.handleMessage(message);
		
	    List<String> openConns = connFactory.getOpenConnectionIds();
	    if(null == openConns || openConns.size() == 0){

	        throw new Exception("No Client connection registered");
	    }

	    for (String connId: openConns) {
	    	Message<String> msg = MessageBuilder.withPayload("text out")
	    			.setHeader(IpHeaders.CONNECTION_ID, connId)
//	    			.setReplyChannel(outChannel)
	    			.build();
	    	messageHandler.handleMessage(msg);
//	    	outChannel.send(msg);
	    }		
		
	}
	
	
}
