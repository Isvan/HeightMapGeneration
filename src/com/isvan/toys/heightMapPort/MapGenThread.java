package com.isvan.toys.heightMapPort;

import java.util.ArrayList;

import javax.swing.JEditorPane;

import com.isvan.toys.heightMapPort.dataStructures.DataPoint;
import com.isvan.toys.heightMapPort.interfaces.MapThreadDoneListener;

public class MapGenThread extends Thread {

	int id;
	int width;
	int height;
	int heightOffSet;
	int maxNum;

	double maxWidthStrength;
	int widthStrengthVariance;

	double max;
	double min;

	float strength;

	ArrayList<DataPoint> lakes;
	ArrayList<DataPoint> mountains;
	MapThreadDoneListener lis;

	int[] map;

	boolean startNormalize;

	public MapGenThread(int id, int width, int height,
			ArrayList<DataPoint> lakes, ArrayList<DataPoint> mountains,
			MapThreadDoneListener lis) {
		this.width = width;
		this.height = height;
		this.heightOffSet = id * height;
		this.id = id;
		this.lakes = lakes;
		this.mountains = mountains;
		this.lis = lis;
		maxNum = (int) Math.pow(2, 16);

		max = Double.MIN_VALUE;
		min = Double.MAX_VALUE;

		maxWidthStrength = 12;
		widthStrengthVariance = 12;

		map = new int[width * height];

		startNormalize = false;

		strength = 0.7f;

	}

	public void run() {

		addMountains();
		addLakes();
	//	dampen();
		findMaxMin();

		// System.out.println("Value at 50 " + map[50]);
		lis.preNormDone(id, max, min);

		while (!startNormalize) {
			try {
				sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
		}

		lis.sendFinishedMap(id, map);

		// System.out.println("Thread Done : " + id);
	}

	public void normalize(double baseMin, double baseMax) {
		startNormalize = true;
		/*
		 * for(int i = 0;i < map.length;i++){ map[i] += baseMin * -1; if(map[i]
		 * < 0){ // System.out.println("Rounding Error Low End"); map[i] = 0; }
		 * }
		 */

		for (int i = 0; i < map.length; i++) {
			int val = map[i];
			val = (int) MathUtils.scale(val, baseMin, baseMax, 0, maxNum - 1);
			map[i] = val;
			// System.out.println("Scaled Value : " + val);
			if (map[i] >= maxNum) {
				System.out.println("Rounding Error High End  : " + val);

				map[i] = maxNum;
			}
			
			if (map[i] < 0) {
				System.out.println("Rounding Error Low End  : " + val);

				map[i] = 0;
			}
			
			
			
		}

	}

	public void findMaxMin() {
		for (int i = 0; i < map.length; i++) {
			if (map[i] > max) {
				max = map[i];
			} else if (map[i] < min) {
				min = map[i];
			}
		}
	}

	public void addMountains() {

		for (DataPoint P : mountains) {
			int pointX = P.X;
			int pointY = P.Y;

			for (int i = 0; i < height; i++) {
				for (int k = 0; k < width; k++) {

					double distance = MathUtils.distance(i + heightOffSet, k,
							pointX, pointY);

					double pointHeight = MathUtils.gaussianFunction(maxNum,
							width
									/ (maxWidthStrength - P.strength
											* widthStrengthVariance), 0,
							distance);

					map[width * i + k] += pointHeight;

				}
			}
		}
	}

	public void addLakes() {

		for (DataPoint P : lakes) {
			int pointX = P.X;
			int pointY = P.Y;

			for (int i = 0; i < height; i++) {
				for (int k = 0; k < width; k++) {

					// 255));

					/*
					 * double distance = MathUtils.distance(i + heightOffSet, k,
					 * pointX, pointY) (maxWidthStrength - widthStrengthVariance
					 * P.strength); // double scale = MathUtils.scale(distance,
					 * 0, // (width * 2) * 2, 0, Math.pow(2, 16)); map[width * i
					 * + k] -= Math.round(P.strength (Integer.MAX_VALUE -
					 * distance));
					 */

					double distance = MathUtils.distance(i + heightOffSet, k,
							pointX, pointY);

					double pointHeight = MathUtils.gaussianFunction(maxNum,
							width
									/ (maxWidthStrength - P.strength
											* widthStrengthVariance), 0,
							distance);

					map[width * i + k] -= pointHeight;

				}
			}
		}
	}

	public void dampen() {

		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {

				// 255));
				/*
				 * map[width * i + k] -= Math.round(0.2 * (maxNum - MathUtils
				 * .scale(Math.round(MathUtils.distance(i+heightOffSet, k,
				 * pointX, pointY) * 10), 0, (width * 2) * 2, 0, Math.pow(2,
				 * 16))));
				 */
				map[width * i + k] -= 1000 * MathUtils.distance(i + heightOffSet, k,
						width / 2, width / 2);

				// img[cols * i + k] = (int) (0.5 * maxNum);

			}
		}

		/*
		 * img[cols * i + k] -= Math .round(0.50 * (MathUtils.scale(Math
		 * .round(MathUtils.distance(i, k, cols/2, rows/2) * 10), 0, (cols +
		 * rows)*2, 0, Math .pow(2, 16))));
		 */

	}
}
