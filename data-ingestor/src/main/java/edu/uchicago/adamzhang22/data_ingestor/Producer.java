/* 
* Licensed to the Apache Software Foundation (ASF) under one or more
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
package edu.uchicago.adamzhang22.data_ingestor;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

public class Producer {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        
        
        // Consume files to a content-based router
        // EIP 1: CONTENT-BASED ROUTER
        // EIP 2: INVALID MESSAGE CHANNEL
        // EIP 3: Wiretap
        // EIP 4: Load Balancer
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("file:data/inbox?move=./../outbox")
        		.wireTap("jms:queue:tick_audit")
        		.choice()
        			.when(header("CamelFileName").endsWith(".csv"))
        				.to("direct:csv_ticks")
        			.when(header("CamelFileName").endsWith(".json"))
        				.to("direct:json_ticks")
        			.otherwise()
        				.to("jms:queue:bad_ticks")
        		.end();
        	}
        });
        
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("direct:csv_ticks")
        		.loadBalance().failover(1,false,true)
        		.to("jms:queue:csv_ticks1").to("jms:queue:csv_ticks2").to("jms:queue:csv_ticks3");
        	}
        });
        
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("direct:json_ticks")
        		.loadBalance().failover(1,false,true)
        		.to("jms:queue:json_ticks1").to("jms:queue:json_ticks2").to("jms:queue:json_ticks3");
        	}
        });
        
        // start the route and let it do its work
        context.start();
        Thread.sleep(9999999);

        // stop the CamelContext yourself once you make sure all csv files are on queue
        context.stop();
    }
}
