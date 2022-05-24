package edu.uchicago.adamzhang22.analytics_engine;
import java.util.List;


public class StandardDeviation extends DispersionCalculator {
	
	
	@Override
	public double dispersionSum(double mean, List<Double> data) {
		double sum = 0.0;
		for (double d : data) {
			sum += Math.pow(d - mean, 2);
		}
		return sum;
	}

	@Override
	public double aggregateDispersion(double sum, List<Double> data) {
		double res = sum / (data.size() - 1);
		return Math.sqrt(res);
	}
	
}
