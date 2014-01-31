package de.CollectorTest;

import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread {
	final int roundsToLive = Benchmark.roundsToLive;
	final int overAllRounds = Benchmark.overAllRounds;
	final int arrLength = Benchmark.arrLength;
	final short objectSize = Benchmark.objectSize;
	
	final int bigObjects = (int) (arrLength*0.01);
	int[] countArr = new int[roundsToLive];
	int currentRound;
	int livingObjects = 0;
	int dyingObjects = 0;
	long kmh = 0;
	long kmh1 = 0;
	long kmh2 = 0;
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
		//	init flat graph
		for (int i = 1; i <= roundsToLive; i++) {
			Benchmark.data.addValue(0.0, "", Integer.toString(i));
		}
		
		while ((currentRound < overAllRounds) && !forcedShutDown.get()) {
			livingObjects = 0;
			dyingObjects = 0;
			kmh1 = System.currentTimeMillis();
			countArr = new int[roundsToLive];
			
			
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
					countArr[randomizedObjects[i].getRoundsToLive() - 1]++;
				} else {
					dyingObjects++;
					randomizedObjects[i] = generateRandomizedObject(i);
					countArr[randomizedObjects[i].getRoundsToLive() - 1]++;
				}
			}
			
			currentRound++;
			
			// sleep
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("could not sleep while working");
			}
			
			//	graph data
			for (int i = 0; i < roundsToLive; i++) {
				Benchmark.data.setValue(countArr[i] , "", Integer.toString(i + 1));
			}
			
			kmh2 = kmh2 + (1000/(System.currentTimeMillis()-kmh1));
			
			if (currentRound % 5 == 0) {
				kmh = kmh2 / 5;
				kmh2 = 0;
			}
			
			Benchmark.console.setText("Derzeitige Runde:      " + currentRound + "\nGesamte Runden:     " + overAllRounds + "\n----------\nÜberlebten dieser Runde:         " + livingObjects + "\nStarben diese Runde:                " + dyingObjects + "\nRunden pro Sekunde:                " + kmh);
		}
		
		Benchmark.isRunning = false;
		Benchmark.startStopWorker.setText("Test starten");
		Benchmark.statusText.setText("Test Beendet");
		Benchmark.consolePanel.add(Benchmark.pauseLabel);
		Benchmark.consolePanel.remove(Benchmark.loadingLabel);
		Benchmark.consolePanel.repaint();
		Benchmark.setSlidersEditable(true);
	}
	
	//	initialize working array of randomized objects
	public void init() {
		for (int i = 0; i < randomizedObjects.length; i++) {
				randomizedObjects[i] = generateRandomizedObject(i);
		}
	}
	
	//	generates a randomized object based on the position in the array
	private RandomizedObject generateRandomizedObject(int iterator) {
		if (iterator == 0){
			return new RandomizedObject(currentRound, 2, roundsToLive, objectSize);
		}
		
		int objectsRoundstoLive = (int) (( ( (arrLength * 2) + 1 ) / ( (iterator + 1) - 1 ) ) - 1);
		
		if (objectsRoundstoLive < 1){
			objectsRoundstoLive = 1;
		}
		
		if (objectsRoundstoLive > roundsToLive){
			objectsRoundstoLive = roundsToLive;
		}
		
		return new RandomizedObject(currentRound, objectsRoundstoLive, roundsToLive, objectSize);
	}
	
}