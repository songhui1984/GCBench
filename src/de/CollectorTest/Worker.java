package de.CollectorTest;

import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {
	final int roundsToLive = Benchmark.roundsToLive;
	final int overAllRounds = Benchmark.overAllRounds;
	final int arrLength = Benchmark.arrLength;
	
	final int bigObjects = (int) (arrLength*0.01);
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
					if (i < bigObjects) {
						randomizedObjects[i] = new RandomizedObject(currentRound, roundsToLive, true);
					} else {
						randomizedObjects[i] = new RandomizedObject(currentRound, roundsToLive, false);
					}
				}
			}
			
			currentRound++;
			
			// sleep
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("could not sleep while working");
			}
			
			Benchmark.console.setText("Current round:  " + currentRound + "\nOverAll Rounds: " + overAllRounds + "\n----------\nLiving:         " + livingObjects + "\nDead:           " + dyingObjects);
//			System.out.println("   Round: " + currentRound);
//			System.out.println("\n\n\n\n\n\nCurrent round:  " + currentRound + "\nOverAll Rounds: " + overAllRounds + "\n----------\nLiving:         " + livingObjects + "\nDead:           " + dyingObjects );
		}
		
//		System.out.println("Workerloop beendet");
	}
	
	//	initialize working array of randomized objects
	public void init() {
		for (int i = 0; i < randomizedObjects.length; i++) {
			if (i < bigObjects) {
				randomizedObjects[i] = new RandomizedObject(currentRound, roundsToLive, true);
			}else {
				randomizedObjects[i] = new RandomizedObject(currentRound, roundsToLive, false);
			}
		}
	}
	
}