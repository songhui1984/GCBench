package de.CollectorTest;

import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {
	final int roundsToLive = Benchmark.roundsToLive;
	final int overAllRounds = Benchmark.overAllRounds;
	final int arrLength = Benchmark.arrLength;
	
	final int bigObjects = (int) (arrLength*0.01);
	int[] countArr = new int[roundsToLive];
	int currentRound;
	int livingObjects = 0;
	int dyingObjects = 0;
	RandomizedObject[] randomizedObjects = new RandomizedObject[arrLength];
	AtomicBoolean forcedPause = new AtomicBoolean(false);
	AtomicBoolean forcedShutDown = new AtomicBoolean(false);
	
	public void run() {
		init();
		work();
	}
	
	public void pauseWorker() {
		forcedPause.set(true);
	}
	
	public void continueWorker() {
		forcedPause.set(false);
	}
	
	public void shutDownWorker() {
		forcedShutDown.set(true);
	}
	
	//	produce workload for the collector
	public void work() {
//		System.out.println("Neuer Workerloop gestartet");
		
		while ((currentRound < overAllRounds) && !forcedShutDown.get()) {
			livingObjects = 0;
			dyingObjects = 0;
			
			while (forcedPause.get()) {
				try {
					sleep(300);
				} catch (InterruptedException e) {
					System.out.println("could not sleep to make a pause");
				}
			}
			
			//	the working loop
			for (int i = 0; i < arrLength; i++) {
				if (randomizedObjects[i].getRoundsToLive() > (currentRound - randomizedObjects[i].getStartRound())) {
					livingObjects++;
				} else {
					dyingObjects++;
					randomizedObjects[i] = generateRandomizedObject(i);
				}
			}
			
			currentRound++;
			
			// sleep
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("could not sleep while working");
			}
			
			//	TODO graph data
			
			Benchmark.console.setText("Current round:  " + currentRound + "\nOverAll Rounds: " + overAllRounds + "\n----------\nSurvived this round:         " + livingObjects + "\nDied in this round:           " + dyingObjects);
		}
		
//		System.out.println("Workerloop beendet");
	}
	
	//	initialize working array of randomized objects
	public void init() {
		for (int i = 0; i < randomizedObjects.length; i++) {
				randomizedObjects[i] = generateRandomizedObject(i);
		}
	}
	
	//	generates a randomized object based on the position in the array
	private RandomizedObject generateRandomizedObject(int iterator) {
		if (iterator == 0) return new RandomizedObject(currentRound, 1, roundsToLive);
		int objectsRoundstoLive = ( ( (arrLength * 2) + 1 ) / ( (iterator + 1) - 1 ) ) - 1;
		
		countArr[objectsRoundstoLive] = countArr[objectsRoundstoLive] + 1;
		
		return new RandomizedObject(currentRound, objectsRoundstoLive, roundsToLive);
	}
	
}