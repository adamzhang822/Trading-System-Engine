/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uchicago.adamzhang22.analytics_main;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import edu.uchicago.adamzhang22.analytics_engine.AnalyticsEngine;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

public class AnalyticsProducer {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
               
        //  Dequeue tick data and add them to analytics engine
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:ORCL_tick_analytics").
        		process(new AnalyticsProcessor());
        	}
        });
        
        //  Dequeue tick data and add them to analytics engine
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:IBM_tick_analytics").
        		process(new AnalyticsProcessor());
        	}
        });
        
        //  Dequeue tick data and add them to analytics engine
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:MSFT_tick_analytics").
        		process(new AnalyticsProcessor());
        	}
        });
        

        // start the route and let it do its work (transport data)
        context.start();
        Thread.sleep(3000);
        context.stop();
        
        context = new DefaultCamelContext();
        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		
        

        // Run the analytics engine to compute all stats
        AnalyticsEngine engine = AnalyticsEngine.getEngine();
        String[] stocks = new String[] {"ORCL", "IBM", "MSFT"};
        String[] stats = new String[] {"AAD", "MaxAD","MedAD", "STDEV"};
        for (String stock : stocks) engine.computeStats(stock);
        ProducerTemplate template = context.createProducerTemplate();
        
        context.start();
        
        // Send the stats to JMS topic
        for (String stock : stocks) {
        	for (String stat : stats) {
        		template.sendBody(String.format("jms:topic:%s_%s", stock, stat),engine.getStat(stock, stat));
        	}
        }
        
        
        Thread.sleep(5000);
        context.stop();
    }
}