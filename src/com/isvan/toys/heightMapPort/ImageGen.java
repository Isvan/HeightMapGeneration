package com.isvan.toys.heightMapPort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;

public class ImageGen {

	public ImageGen(){
		
	}
	
	public void generateImage(int[] img,int w,int h,String outputFile){

		long timeStart = System.currentTimeMillis();
		
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(new File(outputFile));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fileOut == null) {
			return;
		}

		ImageInfo imi = new ImageInfo(w, h, 16, false,true,false);
		PngWriter png = new PngWriter(fileOut, imi);
		//png.getMetadata().setDpi(100.0);
		png.getMetadata().setTimeNow();
		ImageLineInt iline = new ImageLineInt(imi);
		//ImageLineByte iline2 = new ImageLineByte(imi);
		
		//System.out.println(iline.imgInfo.channels);
		
		for (int k = 0; k < png.imgInfo.rows ; k++) {
			
			for (int i = 0; i < iline.getScanline().length; i++) {
			//	ImageLineHelper.setPixelRGB8(iline, i, (i+k)/2, (i+k)/2, (i+k)/2);
				//ImageLineHelper.se
				///iline.getScanline()[i] = i;
		//		iline.imgInfo.channels
				
				//iline.getScanline()[i] = img[(i+1)*(k+1)];
				iline.getScanline()[i] = img[w*k + i];
				//System.out.println("Input : " + img[w*k + i] + " stored : " + iline.getScanline()[i]);
			}

			png.writeRow(iline);

		}

		
		png.end();
		png.close();
		System.out.println("Done");
		System.out.println("Time taken to draw Map : " + (System.currentTimeMillis() - timeStart));
	}

}
