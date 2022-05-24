package edu.uchicago.adamzhang22.accounts;

import java.util.Map;

public class Neutral implements StateOfMind {

	@Override
	public Map<String, String> evaluateRecs(Map<String, String> recs) {
		return recs;
	}

}
