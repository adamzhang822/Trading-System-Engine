package edu.uchicago.adamzhang22.robo;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class NoVolatility implements InvestmentStrategy{
	
	
	List<String> params;

	public Map<String, String> makeRecs(Map<String, Map<String, Double>> data) {
		// Step 1: Get simple average of the statistics
		Map<String, Double> avgs = new HashMap<String, Double>();
		for (String stock : data.keySet()) {
			double sum = 0.0;
			for (Double val : data.get(stock).values()) sum += val;
			double avg = sum / data.get(stock).size();
			avgs.put(stock, avg);
		}
		
		// Step 2: Rank them in ascending order
		PriorityQueue<Map.Entry<String, Double>> pq = new PriorityQueue<Map.Entry<String,Double>>((a, b) -> a.getValue().compareTo(b.getValue()));
		for (Map.Entry<String, Double> entry : avgs.entrySet()) {
			pq.offer(entry);
		}
		// Step 3: Make recommendation -> Lowest average is buy, others hold
		Map<String, String> recs = new HashMap<String, String>();
		recs.put(pq.poll().getKey(), "Buy");
		while (!pq.isEmpty()) {
			recs.put(pq.poll().getKey(), "Hold");
		}
		return recs;
	}

	public List<String> getStats() {
		List<String> stats = new LinkedList<String>();
		stats.add("STDEV");
		stats.add("AAD");
		return stats;
	}

}
