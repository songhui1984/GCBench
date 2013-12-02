package de.CollectorTest;

import java.util.Random;

public class RandomizedObject {
	
	static Random rnd = new Random(1337); 
	int startRound;
	int roundsToLive;
	Object memory;
	
	public RandomizedObject(int startRound, int roundsToLive, boolean isBig) {
		this.startRound  = startRound;
		float multiply = rnd.nextFloat();
		
		if (isBig) {
			this.roundsToLive = (int) (rnd.nextFloat()*roundsToLive);
			if (this.roundsToLive < (roundsToLive*0.5)) this.roundsToLive = (int) (this.roundsToLive + (roundsToLive*0.5));
		} else {
			this.roundsToLive = (int) (rnd.nextFloat()*(roundsToLive*0.02));
			multiply = rnd.nextFloat()*0.05f;
			if (this.roundsToLive < 1) this.roundsToLive = 1;
		}
		
		memory = new byte[(int) (8192*1024*multiply)];	//	max. 8 megabyte data
	}
	
	public int getRoundsToLive() {
		return roundsToLive;
	}
	
	public int getStartRound() {
		return startRound;
	}
	
}