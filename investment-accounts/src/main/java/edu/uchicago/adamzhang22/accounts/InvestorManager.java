package edu.uchicago.adamzhang22.accounts;
import java.util.List;
import java.util.LinkedList;

public class InvestorManager {
	public static void main(String[] args) throws InterruptedException {
		StateOfMind diamondHands = new DiamondHands();
		StateOfMind panicSelling = new PanicSelling();
		StateOfMind neutral = new Neutral();
		
		List<String> sub1 = new LinkedList<String>();
		sub1.add("robo1");
		sub1.add("robo2");
		
		List<String> sub2 = new LinkedList<String>();
		sub2.add("robo3");
		
		InvestmentAccount investor = new InvestmentAccount(diamondHands, sub1, "investor1");
		InvestmentAccount investor2  = new InvestmentAccount(panicSelling, sub1, "investor2");
		InvestmentAccount investor3 = new InvestmentAccount(neutral, sub2, "investor3");
		
		Thread t = new Thread(investor);
		Thread t2 = new Thread(investor2);
		Thread t3 = new Thread(investor3);
		t.start();
		t2.start();
		t3.start();
		t.join();
		t2.join();
		t3.join();
		
	}
}
