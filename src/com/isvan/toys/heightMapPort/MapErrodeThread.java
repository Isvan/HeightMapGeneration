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
			int[] errpsionMap,ArrayList<DataPoint> mountainLoc) {
		this.mountainLoc = mountainLoc;
		this.inputMap = inputMap;
		this.errosionMapPart = errpsionMap;
		this.height = height;
		this.width = width;
	}

	public void run() {

		for(DataPoint P : mountainLoc){
			errode(P.X,P.Y,inputMap,errosionMapPart);
		}
		
	}

	// Recursive Function to generate errosion map
	//InputMap should be the created heightMap
	//ErrosionMap should be an array full of 1's
	public void errode(int x, int y, int[] inputMap, int[] errosionMap) {
		if(marked(errosionMap[width * y + x])){
			System.out.println("Attempted to errode a marked location ");
			return;
		}
		
		errosionMap[width * y + x] += 1;
		
		int lowX = 0;
		int lowY = 0;
		
		
		//Its a grid ie :
		// -1,-1	-1,0 	-1,1
		// 0,-1		0,0		0,1
		// 1,-1		1,0		1,1
		
		int currentHight = inputMap[width * y + x];
		
		for(int i = -1;i < 2;i++){
			for(int k = -1;k < 2;k++){
		
				if(i == 0 && k == 0){
					continue;
				}
				
				
			}
		}
		
	}

	public boolean inArray(int x,int y){
		if(y > height){
			return false;
		}
		
		if(x > width){
			return false;
		}
		
		return true;
	}
	
	
	public int mark(int valToMark) {
		return valToMark | 0x8000;
	}

	public Boolean marked(int valToCheck) {
		return (valToCheck & 0x8000) == 0x8000;
	}

}
