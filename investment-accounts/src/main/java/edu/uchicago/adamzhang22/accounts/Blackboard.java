package edu.uchicago.adamzhang22.accounts;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import java.util.Map;
import java.util.HashMap;

public class Blackboard implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			Map<String, Integer> scores = new HashMap<String, Integer>();
			Map<String, String> newRecs = newExchange.getIn().getBody(java.util.Map.class);
			for (String stock : newRecs.keySet()) {
				String rec = newRecs.get(stock);
				if (rec.compareTo("Sell") == 0) {
					scores.put(stock, -2);
				} else if (rec.compareTo("Buy") == 0) {
					scores.put(stock, 1);
				} else {
					scores.put(stock, 0);
				}
			}
			newExchange.getIn().setBody(scores);
			return newExchange;
		}
		Map<String, String> newRecs = newExchange.getIn().getBody(java.util.Map.class);
		Map<String, Integer> scores = oldExchange.getIn().getBody(java.util.Map.class);
		for (String stock : newRecs.keySet()) {
			String rec = newRecs.get(stock);
			if (rec.compareTo("Sell") == 0) {
				scores.put(stock, scores.getOrDefault(stock, 0) - 2);
			} else if (rec.compareTo("Buy") == 0) {
				scores.put(stock, scores.getOrDefault(stock, 0) + 1);
			} else {
				scores.put(stock, scores.getOrDefault(stock, 0) + 0);
			}
		}
		oldExchange.getIn().setBody(scores);
		return oldExchange;
	}
	
}
