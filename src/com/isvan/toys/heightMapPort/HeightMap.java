package com.isvan.toys.heightMapPort;

import java.util.ArrayList;
import java.util.Random;

import com.isvan.toys.heightMapPort.dataStructures.DataPoint;
import com.isvan.toys.heightMapPort.dataStructures.Options;
import com.isvan.toys.heightMapPort.interfaces.MapThreadDoneListener;

public class HeightMap implements MapThreadDoneListener {

	boolean mapCreated;

	Options O;

	int center;
	int threadsDone;
	int threadsDoneNeeded;

	double maxVal;
	double minVal;

	int[] finalMap;
	int[] errosionMap;
	
	ArrayList<int[]> finishedMapParts;
	ArrayList<MapGenThread> mapGenThreads;
	ArrayList<MapErrodeThread> mapErrodeThreads;

	
	ArrayList<DataPoint> mountains;
	ArrayList<DataPoint> lakes;
	Random rand;

	long timeStart;

	public HeightMap(Options O) {
		timeStart = System.currentTimeMillis();

		maxVal = Double.MIN_VALUE;
		minVal = Double.MAX_VALUE;

		mapCreated = false;
		this.O = O;
		center = O.height / 2;
		finalMap = new int[O.width * O.height];
		errosionMap = new int[O.width * O.height];
		finishedMapParts = new ArrayList<int[]>();

		// Add dummy objects so we can replace them later
		
		for (int i = 0; i < O.threads; i++) {
			finishedMapParts.add(null);
		}

		// Counter used to tell when all the threads are done working on a task
		threadsDone = 0;

		// Basically add the numbers 0+1 - 8+1 together, then as the threads end
		// add
		// them to threadsDone, and check if equal to threadsDoneNeeded, if they
		// are then all are done
		for (int i = 0; i < O.threads; i++) {
			threadsDoneNeeded += i + 1;
		}

		// Create arrylists of points, theas are given to each thread so they
		// know where to place moutains and lakes
		//
		mountains = new ArrayList<DataPoint>();
		lakes = new ArrayList<DataPoint>();

		// Use a seed so if needed can create the same map again
		rand = new Random(O.seed);

		for (int i = 0; i < O.lakes; i++) {
			lakes.add(new DataPoint((int) (rand.nextDouble() * O.width),
					(int) (rand.nextDouble() * O.height), rand.nextDouble()));
		}

		for (int i = 0; i < O.mountains; i++) {
			mountains.add(new DataPoint((int) (rand.nextDouble() * O.width),
					(int) (rand.nextDouble() * O.height), rand.nextDouble()));
		}

	}

	public void generateMap() {
		mapGenThreads = new ArrayList<MapGenThread>();
		mapErrodeThreads = new ArrayList<MapErrodeThread>();
		
		
		int heightPart = O.height / O.threads;
		int heightLeftOver = O.height % O.threads;
		int count = 0;

		System.out.println("Normal height : " + heightPart);
		System.out.println("Height Left over : " + heightLeftOver);

		for (int i = 0; i < O.threads; i++) {

			if (i == O.threads - 1) {
				// For when the dimensions don't divide nicely by the # of
				// threads
				mapGenThreads.add(new MapGenThread(count, O.width, heightPart,
						heightLeftOver, lakes, mountains, this));

			} else {
				mapGenThreads.add(new MapGenThread(count, O.width, heightPart, 0,
						lakes, mountains, this));
			}

			// threads.add(new MapGenThread(count, O.width, heightPart, lakes,
			// mountains, this));
			count++;

			mapGenThreads.get(i).start();
		}

		System.out.println("Started Threads");

	}

	@Override
	public void sendFinishedMap(int id, int[] map) {

		// Do plus one as thead 0 will cause issues if it ends last
		// The +1 makes sure that all the threads finish
		threadsDone += id + 1;
		finishedMapParts.set(id, map);

		// System.out.println("Thead : " + id + " finsished normalization");

		if (threadsDone == threadsDoneNeeded) {

			generateFinalMap();
			System.out.println("Time taken to generate map : "
					+ (System.currentTimeMillis() - timeStart));
			System.out.println("Time to start errode");
			startErrode();
			outputMap();
		}

	}

	private void generateFinalMap() {

		int finalMapPos = 0;
		for (int i = 0; i < finishedMapParts.size(); i++) {
			for (int k = 0; k < finishedMapParts.get(i).length; k++) {

				finalMap[finalMapPos++] = finishedMapParts.get(i)[k];

			}
		}

	}

	public void outputMap() {
		ImageGen gen = new ImageGen();
		gen.generateImage(finalMap, O.height, O.width, O.fileName);
		mapCreated = true;
	}

	private void normalizeMap() {
		for (MapGenThread M : mapGenThreads) {
			// Threads may be asleep as they are waiting, wake them up
			M.interrupt();
			M.baseMin = minVal;
			M.baseMax = maxVal;
			M.startNormalize = true;
		}
	}

	@Override
	public void preNormDone(int id, double max, double min) {
		// TODO Auto-generated method stub
		if (max > maxVal) {
			maxVal = max;
		}

		if (min < minVal) {
			minVal = min;
		}

		// System.out.println("Thead " + id + " done preNrom");

		threadsDone += id + 1;
		if (threadsDone == threadsDoneNeeded) {

			threadsDone = 0;
			//System.out.println("Max Val : " + maxVal);
		//	System.out.println("Min Val : " + minVal);

			normalizeMap();
		}

	}

	private void startErrode(){
		//Do just one thread for sake of debugging errosion algorithm 
		mapErrodeThreads.add(new MapErrodeThread(O.width, O.height, finalMap, errosionMap, mountains,this));
		for(MapErrodeThread M : mapErrodeThreads){
			M.start();
			
		}
	}
	
	@Override
	public void errodeDone(int id, int[] map) {
		// TODO Auto-generated method stub
		ImageGen gen = new ImageGen();
		gen.generateImage(map, O.height, O.width,"EMap.png");
		System.out.println("Errosion done");
	}

}
