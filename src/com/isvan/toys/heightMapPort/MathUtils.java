package com.isvan.toys.heightMapPort;

public class MathUtils {

	 public static double scale( double valueIn,  double baseMin,  double baseMax,  double limitMin,  double limitMax) {
	        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
	    }
	public static double distance(int x1,int y1,int x2,int y2){
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public static double gaussianFunction(double heightMod,double widthMod,double distanceMod,double xVal){
		
		
		return heightMod * Math.pow(Math.E,-1 * Math.pow(xVal - distanceMod, 2)/(2*widthMod*widthMod));
	}
	
}
