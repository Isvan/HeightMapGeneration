package com.isvan.toys.heightMapPort.interfaces;

public interface MapThreadDoneListener {

	public void sendFinishedMap(int id,int[] map);
	public  void  preNormDone(int id,double max,double min);
}
