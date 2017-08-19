package com.samsonan.tcpserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * https://www.javacodegeeks.com/2014/05/spring-integration-4-0-a-complete-xml-free-example.html
 * https://stackoverflow.com/questions/19188360/how-do-i-create-a-tcp-inbound-gateway-which-detects-connection-loss-and-auto-rec
 * https://stackoverflow.com/questions/41211668/spring-integration-tcp-server-send-message-to-tcp-client
 * 
 *
 */
@Configuration
//@ComponentScan("com.samsonan.integration.endpoint")				//@Component
//@IntegrationComponentScan("com.samsonan.integration.gateway")		//@MessagingGateway
// 1. channel(requestChannel) -> 2.1. transformer(toString) & 2.2. serviceActivator(store DB) -> channel(responseChannel) ->  
@EnableIntegration
public class IntegrationConfiguration {

	private final int port = 1337;//SocketUtils.findAvailableTcpPort();

	
	
//	@Bean
//	public TcpReceivingChannelAdapter inboundAdapter() {
//		TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
//		adapter.setConnectionFactory(serverCF());
//		return adapter;
//	}
	
	
	/**
	 * 1. Server Connection Factory
	 * @return
	 */
	@Bean
	public AbstractServerConnectionFactory serverCF() {
		System.out.println("serverCF created. port:" + this.port);
		return new TcpNetServerConnectionFactory(this.port);
	}

	/**
	 * 2. Inbound TCP GW
	 */
	@Bean
	public TcpInboundGateway tcpInGate(AbstractServerConnectionFactory connectionFactory)  {
		TcpInboundGateway inGate = new TcpInboundGateway();
		inGate.setConnectionFactory(connectionFactory);
		inGate.setRequestChannel(requestChannel());
		inGate.setReplyChannel(outChannel());
		return inGate;
	}
	
	/**
	 * @MessagingGateway - is a business interface to indicate it is a gateway between the end-application and integration layer. 
	 * It is an analogue of <gateway /> component from Spring Integration XML configuration. 
	 * Spring Integration creates a Proxy for this interface and populates it as a bean in the application context. 
	 * The purpose of this Proxy is to wrap parameters in a Message<?> object and send it to the MessageChannel 
	 * according to the provided options.
	 */
	@MessagingGateway                                             
	public interface BotLocator {

	  	@Gateway(requestChannel = "requestChannel")                    
	  	void receive(String inputStr);                            

	}	

    @Bean
    @Description("Entry to the messaging system through the gateway.")
    public MessageChannel requestChannel() {
        return new DirectChannel(); // p2p channel
    }

    @Bean
    public MessageChannel outChannel() {
        return new DirectChannel(); // p2p channel
    }
    
    
//    @Bean
//    @Description("Sends web service responses to both the client and a database filter")
//    public MessageChannel responseChannel() {
//        return new PublishSubscribeChannel(); // 
//    }	

    @Bean
    @Description("Stores non filtered messages to the database")
    public MessageChannel storeChannel() {
        return new DirectChannel();
    }    
    
    @Bean
    @ServiceActivator(inputChannel = "storeChannel")
    public MessageHandler pgAdapter() throws Exception {
        
    	return new MessageHandler() {
			
			@Override
			public void handleMessage(Message<?> msg) throws MessagingException {
				System.out.println("Message " + msg + " received and processed via pgAdapter");
			}
		};

    }    

//	@Transformer(inputChannel="requestChannel")
	@Transformer(inputChannel="requestChannel", outputChannel="outChannel")
	public String convert(byte[] bytes) {
		
		String result = new String(bytes);
		
		System.out.println("convert. result: " + result);
		
		return result;
		
	}    

//	@Transformer(inputChannel="requestChannel", outputChannel="outChannel")
//	public String pass(String str) {
//		
//		return str;
//		
//	} 
	
//	@Transformer(inputChannel="outChannel")
//	public String toConsole(String msg) {
//		
//		System.out.println("toConsole: " + msg);
//		
//		return msg;
//		
//	}    
	
    
//	@MessageEndpoint
//	public static class Echo {
//
//		@Transformer(inputChannel="fromTcp", outputChannel="toEcho")
//		public String convert(byte[] bytes) {
//			return new String(bytes);
//		}
//
//		@ServiceActivator(inputChannel="toEcho")
//		public String upCase(String in) {
//			return in.toUpperCase();
//		}
//
//		@Transformer(inputChannel="resultToString")
//		public String convertResult(byte[] bytes) {
//			return new String(bytes);
//		}
//
//	}


}
