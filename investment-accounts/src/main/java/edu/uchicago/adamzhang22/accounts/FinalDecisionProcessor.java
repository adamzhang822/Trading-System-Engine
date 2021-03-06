package edu.uchicago.adamzhang22.accounts;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.util.Map;
import java.util.HashMap;

public class FinalDecisionProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Integer> scores = exchange.getIn().getBody(java.util.Map.class);
		Map<String, String> finalRecs = new HashMap<String, String>();
		for (String stock : scores.keySet()) {
			if (scores.get(stock) > 0) {
				finalRecs.put(stock, "Buy");
			} else if (scores.get(stock) < 0) {
				finalRecs.put(stock, "Sell");
			} else {
				finalRecs.put(stock, "Hold");
			}
		}
		exchange.getIn().setBody(finalRecs);
	}

}
