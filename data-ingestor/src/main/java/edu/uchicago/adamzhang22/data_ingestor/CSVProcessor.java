package edu.uchicago.adamzhang22.data_ingestor;

import org.apache.camel.Processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;

public class CSVProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		
		// Step 1: Get the payload from body
		String[] payload = exchange.getIn().getBody(String.class).split("\t");
		
		// Step 2: Convert to new format and enrich with timestamp
		StringBuilder sb = new StringBuilder();
		sb.append(payload[0].substring(1) + ",");
		sb.append(payload[1] + ",");
		sb.append(payload[2] + "," );
		sb.append(payload[3] + ",");
		sb.append(payload[4].substring(0, payload[4].length() - 1) + "|");
		String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		sb.append(datetime);
		String newFormat = sb.toString();
		
		// Step 3: Replace the message in exchange
		exchange.getIn().setBody(newFormat);
		exchange.getIn().setHeader("ticker", payload[0].substring(1));
		
	}

}
