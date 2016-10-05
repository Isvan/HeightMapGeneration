package com.isvan.toys.heightMapPort;

import java.util.ArrayList;

import com.isvan.toys.heightMapPort.dataStructures.DataPoint;
import com.isvan.toys.heightMapPort.interfaces.MapThreadDoneListener;

public class MapErrodeThread extends Thread {

	int width, height;
	int id;
	int[] inputMap;
	int[] errosionMapPart;

	MapThreadDoneListener lis;

	ArrayList<DataPoint> mountainLoc;

	public MapErrodeThread(int width, int height, int[] inputMap,
			int[] errpsionMap, ArrayList<DataPoint> mountainLoc,
			MapThreadDoneListener lis) {
		this.mountainLoc = mountainLoc;
		this.inputMap = inputMap;
		this.errosionMapPart = errpsionMap;
		this.height = height;
		this.width = width;
		this.lis = lis;
	}

	public void run() {

		for (DataPoint P : mountainLoc) {
			System.out.println("Starting Errode at loc X:" + P.X + " Y:" + P.Y);
			errode(P.X, P.Y, inputMap, errosionMapPart);
			errosionMapPart[width * P.Y + P.X] = 0xFFFF;
		}
		lis.errodeDone(id, errosionMapPart);
	}

	// Recursive Function to generate errosion map
	// InputMap should be the created heightMap

	public void errode(int x, int y, int[] inputMap, int[] errosionMap) {
		if (marked(errosionMap[width * y + x])) {
		//	System.out.println("Attempted to errode a marked location : " + x
		//			+ " , " + y);
			return;
		}

		if(true){
			return;
		}
		// Errode everypoint by at least one
		//errosionMap[width * y + x] += 1;

		int lowX = 0;
		int lowY = 0;

		// Its a grid ie :
		// -1,-1 -1,0 -1,1
		// 0,-1 0,0 0,1
		// 1,-1 1,0 1,1

		int tempHeight = inputMap[width * y + x];

		// Find lowest value of neighbors
		for (int i = -1; i < 2; i++) {
			for (int k = -1; k < 2; k++) {

				if (i == 0 && k == 0) {
					continue;
				}

				if (inArray(x + i, y + k)) {
					// System.out.println("Check " + tempHeight + " vs " +
					// inputMap[width * (y + k) + (x + i)]);
					if (tempHeight < inputMap[width * (y + k) + (x + i)] - errosionMap[width*(y+k) + (x+i)]) {
						lowX = i;
						lowY = k;
						tempHeight = inputMap[width * (y + k) + (x + i)];
					}
				}

			}
		}

		int currentHeight = inputMap[width * y + x];

		// If the lowest point is the current point, then errosion is done
		if (lowX == 0 && lowY == 0) {
			return;
		} else {
			// Find all points that are lower next to the origin point
			// And not marked
			errosionMap[width * (y + lowY) + (x + lowX)] += errosionMap[width
					* y + x];

			errosionMap[width * y + x] = mark(errosionMap[width * y + x]);
			for (int i = -1; i < 2; i++) {
				for (int k = -1; k < 2; k++) {

					if (i == 0 && k == 0) {
						continue;
					}

					if (inArray(x + i, y + k)) {
						// System.out.println("X : " + x + " Y : " + y + " i:k "
						// + i +":" + k);
						// System.out.println("Check " + currentHeight + " vs "
						// + inputMap[width * (y + k) + (x + i)]);
						if (currentHeight > inputMap[width * (y + k) + (x + i)]) {
							// System.out.println("Going to position : "
							// + (x + i)
							// / + " , "
							// + (y + k)
							// + " Height diff = "
							// + (currentHeight - inputMap[width * (y + k)
							// + (x + i)]));
							errode(x + i, y + k, inputMap, errosionMap);
						}
					}

				}
			}
		}

	}

	public boolean inArray(int x, int y) {
		if (y > height - 1 || y < 0) {
			return false;
		}

		if (x > width - 1 || x < 0) {
			return false;
		}

		return true;
	}

	public int mark(int valToMark) {
		return valToMark | 0x8000;
	}

	public int unMark(int valToUnMark) {
		return (valToUnMark << 1) >> 1;
	}

	public Boolean marked(int valToCheck) {
		return (valToCheck & 0x8000) == 0x8000;
	}

}
