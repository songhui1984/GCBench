package de.CollectorTest;

import java.util.Random;

public class RandomizedObject {
	
	static Random rnd = new Random(1337); 
	int startRound;
	int roundsToLive;
	Object memory;
	
	public RandomizedObject(int startRound, int roundsToLive, int maxRoundsToLive, short objectSize) {
		float multiply = rnd.nextFloat();
		
		this.startRound  = startRound;
		
		roundsToLive = (int) ((multiply*0.2 + 0.9) * roundsToLive);
		if (roundsToLive < 1) roundsToLive = 1;
		if (roundsToLive > maxRoundsToLive) roundsToLive = maxRoundsToLive;
		this.roundsToLive = roundsToLive;
		
		//	max. around 4 megabyte data
		float memFactor = roundsToLive / maxRoundsToLive;
		if (multiply < (memFactor - 0.1)) multiply = (float) (memFactor - 0.1);
		if (multiply > (memFactor + 0.1)) multiply = (float) (memFactor + 0.1);
		memory = new byte[(int) (objectSize*1024*multiply)];
	}
	
	public int getRoundsToLive() {
		return roundsToLive;
	}
	
	public int getStartRound() {
		return startRound;
	}
	
}