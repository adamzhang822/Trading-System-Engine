package edu.uchicago.adamzhang22.accounts;
import java.util.List;
import java.util.Random;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import java.util.Map;

public class InvestmentAccount implements Runnable {
	
	
	StateOfMind state;
	List<String> subscriptions;
	String name;
	
	public InvestmentAccount(StateOfMind state, List<String> subscriptions, String name) {
		this.state = state;
		this.subscriptions = subscriptions;
		this.name = name;
	}
	
	public void changeState(StateOfMind state) {
		this.state = state;
	}

	@Override
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
	        
	        for (String subscription : subscriptions) {
	        	context.addRoutes(new RouteBuilder() {
	        		public void configure() {
	        			from(String.format("jms:topic:recs_%s", subscription)).
	        			to("seda:recs_received");
	        		}
	        	});
	        };
	        
	        context.addRoutes(new RouteBuilder() {
	        	public void configure() {
	        		from("seda:recs_received").
	        		aggregate(constant(1), new Blackboard()). 
	        		completionSize(subscriptions.size()).
	        		process(new FinalDecisionProcessor()).
	        		to("seda:tallied");
	        	}
	        });
	        
	        ConsumerTemplate consumer = context.createConsumerTemplate();
	        
	        
	        context.start();
	        Thread.sleep(9000);
	        Map<String, String> res = (Map<String, String>) consumer.receiveBody("seda:tallied");
	        Map<String, String> executedTrades = this.state.evaluateRecs(res);
	        StringBuilder sb = new StringBuilder();
	        sb.append(String.format("Trading decisions of %s \n", this.name));
	        for (String stock : executedTrades.keySet()) {
	        	sb.append(stock + ":" + executedTrades.get(stock) + ",");
	        }
	        System.out.println(sb.toString());
	        
	        // Random walk to another state
	        Random rand = new Random();
	        double double_random = rand.nextDouble();
	        if (double_random <= 0.33) {
	        	this.changeState(new PanicSelling());
	        } else if (double_random > 0.33 && double_random < 0.66) {
	        	this.changeState(new Neutral());
	        } else {
	        	this.changeState(new DiamondHands());
	        }
	        
	        context.stop();
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
