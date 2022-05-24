package edu.uchicago.adamzhang22.analytics_engine;
import java.util.List;

public class MaximumAbsoluteDeviation extends DispersionCalculator {

	
	@Override
	public double dispersionSum(double mean, List<Double> data) {
		double max = Double.MIN_VALUE;
		for (double d : data) {
			max = Math.max(max, Math.abs(d - mean));
		}
		return max;
	}

	@Override
	public double aggregateDispersion(double sum, List<Double> data) {
		return sum;
	}

}
