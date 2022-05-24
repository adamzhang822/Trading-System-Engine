package edu.uchicago.adamzhang22.analytics_engine;
import java.util.List;

public abstract class DispersionCalculator {
	
	public double getStat(List<Double> data) {
		double mean = getMean(data);
		double sum = dispersionSum(mean, data);
		double res = aggregateDispersion(sum, data);
		return res;
	}
	
	public double getMean(List<Double> data) {
		double sum = 0;
		for (Double d : data) sum += d;
		return (double) (sum / data.size());
	}
	
	public abstract double dispersionSum(double mean, List<Double> data);
	
	public abstract double aggregateDispersion(double sum, List<Double> data);
	
	
}
