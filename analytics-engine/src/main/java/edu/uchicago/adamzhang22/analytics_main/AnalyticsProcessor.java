package edu.uchicago.adamzhang22.analytics_main;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import edu.uchicago.adamzhang22.analytics_engine.AnalyticsEngine;

public class AnalyticsProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		String payload = exchange.getIn().getBody(String.class);
		String[] payloads = payload.split(",");
		AnalyticsEngine engine = AnalyticsEngine.getEngine();
		String ticker = payloads[0];
		Double buyPrice = Double.parseDouble(payloads[1]);
		engine.addData(ticker, buyPrice);
	}
}
