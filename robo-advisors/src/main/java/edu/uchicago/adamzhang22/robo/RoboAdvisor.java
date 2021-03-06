package edu.uchicago.adamzhang22.robo;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.List;
import java.util.HashMap;

public class RoboAdvisor implements Runnable {
	private InvestmentStrategy strategy;
	Map<String, Map<String, Double>> data;
	List<String> stocks;
	String name;
	
	public RoboAdvisor(List<String> stocks, InvestmentStrategy strategy, String name) {
		this.stocks = stocks;
		this.strategy = strategy;
		this.data = new HashMap<String, Map<String, Double>>();
		for (String stock : stocks) {
			data.put(stock, new HashMap<String, Double>());
		}
		this.name = name;
	}
	
	public Map<String, Map<String,Double>> getData(){
		return this.data;
	}
	
	public void setInvestmentStrategy(InvestmentStrategy strategy) {
		this.strategy = strategy;
	}
	
	public void run() {	
		
		try {
	        // create CamelContext
	        CamelContext context = new DefaultCamelContext();
	
	        // connect to ActiveMQ JMS broker listening on localhost on port 61616
	        ConnectionFactory connectionFactory = 
	        	new ActiveMQConnectionFactory("tcp://localhost:61616");
	        context.addComponent("jms",
	            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
	               
	        // Subscribe to all relevant topics in ActiveMQ
	        List<String> stats = strategy.getStats();
	        for (String stock : stocks) {
	        	for (String stat : stats) {
	        		context.addRoutes(new RouteBuilder() {
	        			public void configure() {
	        				from(String.format("jms:topic:%s_%s", stock, stat))
	        				.to(String.format("seda:%s_%s_received", stock,stat));
	        			}
	        		});
	        	}
	        }
	        ConsumerTemplate consumer = context.createConsumerTemplate();
	        ProducerTemplate producer = context.createProducerTemplate();
	        
	        // start the route and let it do its work (transport data)
	        context.start();
	        // Give 9 seconds to get data from topic
	        Thread.sleep(9000);

	        // Move from direct to object storage
	        for (String stock : stocks) {
	        	for (String stat : stats) {
	        		Double val = (Double) consumer.receiveBody(String.format("seda:%s_%s_received", stock, stat));
	        		this.data.get(stock).put(stat, val);
	        	}
	        }
	        Map<String, String> rec = this.strategy.makeRecs(data);
	        for (String stock : rec.keySet()) {
	        	System.out.println(stock + ":" + rec.get(stock));
	        }
	        producer.sendBody(String.format("jms:topic:recs_%s",this.name), rec);
	        Thread.sleep(2000);
	        context.stop();
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
