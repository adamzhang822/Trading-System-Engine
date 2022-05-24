package edu.uchicago.adamzhang22.analytics_engine;
import java.util.List;

public class AverageAbsoluteDeviation extends DispersionCalculator {
	
	

	@Override
	public double dispersionSum(double mean, List<Double> data) {
		double sum = 0.0;
		for (double d : data) {
			sum += Math.abs(d - mean);
		}
		return sum;
	}

	@Override
	public double aggregateDispersion(double sum, List<Double> data) {
		return sum / data.size();
	}

}
