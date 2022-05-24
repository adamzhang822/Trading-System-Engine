package edu.uchicago.adamzhang22.accounts;

import java.util.Map;
import java.util.HashMap;

public class PanicSelling implements StateOfMind {

	@Override
	public Map<String, String> evaluateRecs(Map<String, String> recs) {
		Map<String, String> res = new HashMap<String, String>();
		for (String stock : recs.keySet()) {
			if (recs.get(stock).compareTo("Hold") == 0) {
				res.put(stock, "Sell");
			} else {
				res.put(stock, recs.get(stock));
			}
		}
		return res;
	}
	
}
