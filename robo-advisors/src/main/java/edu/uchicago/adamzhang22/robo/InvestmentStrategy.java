package edu.uchicago.adamzhang22.robo;
import java.util.List;
import java.util.Map;

public interface InvestmentStrategy {
	public List<String> getStats();
	public Map<String, String> makeRecs(Map<String, Map<String, Double>> data);
}
