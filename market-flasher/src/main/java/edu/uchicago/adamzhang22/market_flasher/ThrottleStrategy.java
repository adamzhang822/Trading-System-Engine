package edu.uchicago.adamzhang22.market_flasher;

import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.Exchange;

public class ThrottleStrategy implements AggregationStrategy {

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) return newExchange;
		
		String newBody = newExchange.getIn().getBody(String.class);
		oldExchange.getIn().setBody(newBody);
		return oldExchange;
		
	}

}
