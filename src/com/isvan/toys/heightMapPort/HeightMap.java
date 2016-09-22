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

	ArrayList<int[]> finishedMapParts;
	ArrayList<MapGenThread> threads;

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
		finishedMapParts = new ArrayList<int[]>();

		// Add dummy objects so we can replace them later
		// With maps that are done, sometimes triggers a nullpoint, but nothing
		// really happens
		for (int i = 0; i < O.threads; i++) {
			finishedMapParts.add(null);
		}

		// Counter used to tell when all the threads are done working on a task
		threadsDone = 0;

		// Basically add the numbers 0+1 - 8+1 together, then as the threads end add
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
		threads = new ArrayList<MapGenThread>();

		int heightPart = O.height / O.threads;

		int count = 0;
		for (int i = 0; i < O.threads; i++) {
			threads.add(new MapGenThread(count, O.width, heightPart, lakes,
					mountains, this));
			count++;

			threads.get(i).start();
		}

		System.out.println("Started Threads");

	}

	@Override
	public void sendFinishedMap(int id, int[] map) {

		// Do plus one as thead 0 will cause issues if it ends last
		// The +1 makes sure that all the threads finish
		threadsDone += id + 1;
		finishedMapParts.set(id, map);

		if (threadsDone == threadsDoneNeeded) {

			generateFinalMap();
			System.out.println("Time taken to generate map : "
					+ (System.currentTimeMillis() - timeStart));
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
		for (MapGenThread M : threads) {
			//Threads may be asleep as they are waiting, wake them up
			M.interrupt();
			M.normalize(minVal, maxVal);
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
		System.out.println("Thead " + id + " done preNrom");
		threadsDone += id + 1;
		if (threadsDone == threadsDoneNeeded) {

			threadsDone = 0;
			System.out.println("Max Val : " + maxVal);
			System.out.println("Min Val : " + minVal);

			normalizeMap();
		}

	}

}
