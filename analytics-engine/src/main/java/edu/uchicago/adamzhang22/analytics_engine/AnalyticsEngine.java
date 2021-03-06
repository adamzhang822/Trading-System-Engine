package edu.uchicago.adamzhang22.analytics_engine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// Reference for thread-safe singleton:
// https://medium.com/@cancerian0684/singleton-design-pattern-and-how-to-make-it-thread-safe-b207c0e7e368
public class AnalyticsEngine {
	private static AnalyticsEngine engine;
	private HashMap<String, ArrayList<Double>> data;
	
	// HashMap<Ticker, HashMap<StatName, StatVal>>
	private HashMap<String, HashMap<String, Double>> stats;
	DispersionCalculator AAD;
	DispersionCalculator MaxAD;
	DispersionCalculator MedAD;
	DispersionCalculator STDEV;
	
	private AnalyticsEngine() {
		
		// Initialize data containers 
		this.data = new HashMap<String, ArrayList<Double>>();
		data.put("ORCL", new ArrayList<Double>());
		data.put("IBM", new ArrayList<Double>());
		data.put("MSFT", new ArrayList<Double>());
		this.stats = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> orclStatMap = new HashMap<String, Double>();
		stats.put("ORCL", orclStatMap);
		HashMap<String, Double> msftStatMap = new HashMap<String, Double>();
		stats.put("MSFT", msftStatMap);
		HashMap<String, Double> ibmStatMap = new HashMap<String, Double>();
		stats.put("IBM", ibmStatMap);
		
		// Initialize Stats calculators
		AAD = new AverageAbsoluteDeviation();
		MaxAD = new MaximumAbsoluteDeviation();
		MedAD = new MedianAbsoluteDeviation();
		STDEV = new StandardDeviation();
	}
	
	public static AnalyticsEngine getEngine() {
		if (engine == null) {
			synchronized(AnalyticsEngine.class) {
				if (engine == null) {
					engine = new AnalyticsEngine();
				}
			}
		}
		return engine;
	}
	
	public void addData(String ticker, Double price) {
		data.get(ticker).add(price);
	}
	
	public void computeStats(String ticker) {		
		ArrayList<Double> tickerData = data.get(ticker);
		HashMap<String, Double> statsData = stats.get(ticker);
		statsData.put("AAD", AAD.getStat(tickerData));
		statsData.put("MaxAD", MaxAD.getStat(tickerData));
		statsData.put("MedAD", MedAD.getStat(tickerData));
		statsData.put("STDEV", STDEV.getStat(tickerData));

	}
	
	public double getStat(String ticker, String statName) {
		return stats.get(ticker).get(statName);
	}
	
	public Map<String, Double> getStat(String ticker) {
		return stats.get(ticker);
	}
	
}
