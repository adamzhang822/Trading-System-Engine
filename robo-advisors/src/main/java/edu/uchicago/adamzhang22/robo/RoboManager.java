package edu.uchicago.adamzhang22.robo;

import java.util.List;
import java.util.LinkedList;

public class RoboManager {
	public static void main(String[] args) throws InterruptedException {
		InvestmentStrategy middleOfTheRoad = new MiddleOfTheRoad();
		InvestmentStrategy buyVolatile = new BuyVolatile();
		InvestmentStrategy noVolatility = new NoVolatility();
		
		List<String> stocks1 = new LinkedList<String>();
		stocks1.add("ORCL");
		stocks1.add("IBM");
		stocks1.add("MSFT");
		List<String> stocks2 = new LinkedList<String>();
		stocks2.add("ORCL");
		stocks2.add("MSFT");
		
		RoboAdvisor robo = new RoboAdvisor(stocks1, middleOfTheRoad, "robo1");
		RoboAdvisor robo2 = new RoboAdvisor(stocks1, noVolatility, "robo2");
		RoboAdvisor robo3 = new RoboAdvisor(stocks2, buyVolatile, "robo3");
		
		Thread t = new Thread(robo);
		Thread t2 = new Thread(robo2);
		Thread t3 = new Thread(robo3);
		t.start();
		t2.start();
		t3.start();
		t.join();
		t2.join();
		t3.join();
		
		System.out.println("Ran!");
	}
}
