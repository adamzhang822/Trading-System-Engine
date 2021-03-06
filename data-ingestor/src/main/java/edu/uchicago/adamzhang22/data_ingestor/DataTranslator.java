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
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jms.JmsComponent;

public class DataTranslator {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
               
        
        // Process csv and json files
        // EIP 1: MESSAGE TRANSLATOR
        // EIP 2: CANONICAL DATA MODEL
        // EIP 3: DATA ENRICHER
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:csv_ticks1")
        		.unmarshal().csv().split(body())
        		.process(new CSVProcessor()).
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:csv_ticks2")
        		.unmarshal().csv().split(body())
        		.process(new CSVProcessor()).
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:csv_ticks3")
        		.unmarshal().csv().split(body())
        		.process(new CSVProcessor()).
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:json_ticks1")
        		.unmarshal(new JacksonDataFormat(Tick.class))
        		.process(new JSONProcessor()). 
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:json_ticks2")
        		.unmarshal(new JacksonDataFormat(Tick.class))
        		.process(new JSONProcessor()). 
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
        context.addRoutes(new RouteBuilder() {
        	public void configure() {
        		from("jms:queue:json_ticks3")
        		.unmarshal(new JacksonDataFormat(Tick.class))
        		.process(new JSONProcessor()). 
        		choice().
	        		when(body().regex(".*ORCL.*")).to("jms:topic:ORCL_tick"). 
	    			when(body().regex(".*IBM.*")).to("jms:topic:IBM_tick").
	    			when(body().regex(".*MSFT.*")).to("jms:topic:MSFT_tick");
        	}
        });
              
        // start the route and let it do its work
        context.start();
        Thread.sleep(99999);
        context.stop();
    }
}
