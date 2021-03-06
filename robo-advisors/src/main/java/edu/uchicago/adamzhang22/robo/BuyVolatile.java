package edu.uchicago.adamzhang22.robo;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class BuyVolatile implements InvestmentStrategy{
	
	
	List<String> params;

	public Map<String, String> makeRecs(Map<String, Map<String, Double>> data) {
		// Just recommend shorting the stock with highest Maximum Absolute Deviation
		String shortStock = "";
		double highestMaxAD = Double.MIN_VALUE;
		for (String stock : data.keySet()) {
			double maxAd = data.get(stock).get("MaxAD");
			if (maxAd > highestMaxAD) {
				shortStock = stock;
				highestMaxAD = maxAd;
			}
		}
		
		Map<String, String> recs = new HashMap<String, String>();
		for (String stock : data.keySet()) {
			if (stock.compareTo(shortStock) != 0) {
				recs.put(stock, "Hold");
			} else {
				recs.put(stock, "Buy");
			}
		}
		return recs;
	}

	public List<String> getStats() {
		List<String> stats = new LinkedList<String>();
		stats.add("MaxAD");
		return stats;
	}

}