package edu.uchicago.adamzhang22.data_ingestor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class JSONProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		// Step 1: Get the payload from body
		Tick payload = exchange.getIn().getBody(Tick.class);
		
		// Step 2: Cojnvert to new format and enrich with timestamp
		StringBuilder sb = new StringBuilder();
		sb.append(payload.getTicker() + ",");
		sb.append(Double.toString(payload.getBuy_price())+ ",");
		sb.append(Integer.toString(payload.getBuy_quantity()) + "," );
		sb.append(Double.toString(payload.getSell_price()) + ",");
		sb.append(Integer.toString(payload.getSell_quantity()) + ",");
		String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		sb.append(datetime);
		String newFormat = sb.toString();
		
		// Step 3: Replace the message in exchange
		exchange.getIn().setBody(newFormat);
		exchange.getIn().setHeader("ticker", payload.getTicker());
	}

}
