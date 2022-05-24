package edu.uchicago.adamzhang22.analytics_engine;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MedianAbsoluteDeviation extends DispersionCalculator {

	@Override
	public double dispersionSum(double mean, List<Double> data) {
		List<Double> dispersions = new ArrayList<Double>();
		for (double d : data) {
			dispersions.add(Math.abs(d - mean));
		}
		Collections.sort(dispersions);
		return dispersions.get(dispersions.size() / 2);
	}

	@Override
	public double aggregateDispersion(double sum, List<Double> data) {
		return sum;
	}
	
}
