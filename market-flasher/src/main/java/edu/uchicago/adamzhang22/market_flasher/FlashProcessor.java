package edu.uchicago.adamzhang22.market_flasher;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class FlashProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		String content = exchange.getIn().getBody(String.class);
		UpdateFlasher flasher = UpdateFlasher.getFlasher();
		flasher.queueUpdate(content);
	}
	
}
