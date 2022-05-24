package edu.uchicago.adamzhang22.data_ingestor;

public class Tick {
	private String ticker;
	private double buy_price;
	private int buy_quantity;
	private double sell_price;
	private int sell_quantity;
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public double getBuy_price() {
		return buy_price;
	}
	public void setBuy_price(double buy_price) {
		this.buy_price = buy_price;
	}
	public int getBuy_quantity() {
		return buy_quantity;
	}
	public void setBuy_quantity(int buy_quantity) {
		this.buy_quantity = buy_quantity;
	}
	public double getSell_price() {
		return sell_price;
	}
	public void setSell_price(double sell_price) {
		this.sell_price = sell_price;
	}
	public int getSell_quantity() {
		return sell_quantity;
	}
	public void setSell_quantity(int sell_quantity) {
		this.sell_quantity = sell_quantity;
	}
	
	
}
