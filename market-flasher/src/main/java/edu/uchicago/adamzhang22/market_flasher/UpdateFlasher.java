package edu.uchicago.adamzhang22.market_flasher;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors


// Reference for thread-safe singleton:
// https://medium.com/@cancerian0684/singleton-design-pattern-and-how-to-make-it-thread-safe-b207c0e7e368
public class UpdateFlasher {
	private static UpdateFlasher flasher;
	private CopyOnWriteArrayList<String> updates;
	
	private UpdateFlasher() {
		this.updates = new CopyOnWriteArrayList<String>();
	}
	
	
	public static UpdateFlasher getFlasher() {
		if (flasher == null) {
			synchronized (UpdateFlasher.class) {
				if (flasher == null) {
					flasher = new UpdateFlasher();
				}
			}
		}
		return flasher;
	}
	
	public void queueUpdate(String update) {
		updates.add(update);
	}
	
	public void flashUpdates() {
		
		try {
			FileWriter myWriter = new FileWriter("updates.txt");
			for (String update : updates) {
				myWriter.write(update + "\n");
			}
			myWriter.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
