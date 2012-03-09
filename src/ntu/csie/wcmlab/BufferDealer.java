/*this class handle about Bitmaps for undo/redo and save use.*/



package ntu.csie.wcmlab;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;


public class BufferDealer {
	private final int BITMAP_CACHE_SIZE = 2;
	private ArrayList<Bitmap> mBitmaps;
	private boolean isUndoing;
	private int undoCounter;
	
	
	public BufferDealer()
	{
		

		clear();
		
	}
	
	public void onTouchStep(Bitmap bmap,Canvas ca)
	{
		if(isUndoing)
		{
			for(int i = undoCounter;i>0;i--)
			{
				mBitmaps.remove(mBitmaps.size()-1).recycle();

			}
			isUndoing = false;
			undoCounter = 0;
		}
		
		saveBitmap(bmap);

	}
	
	public void saveBitmap(Bitmap bmap) {
		if (mBitmaps.size() > BITMAP_CACHE_SIZE)
			mBitmaps.remove(0).recycle();
		
		

		
		mBitmaps.add(Bitmap.createBitmap(bmap));

	}
	
	//get previous bitmap
	public Bitmap getP() {

		//if (undoCounter < mBitmaps.size() - 1) {
		if(isUndoValid()){
			++undoCounter;
		}

        return mBitmaps.get(mBitmaps.size() - 1 - undoCounter);
	}

	//get next bitmap
	public Bitmap getN() {

		//if (undoCounter > 0) {
		if(isRedoValid()){
			--undoCounter;
		}

		return mBitmaps.get(mBitmaps.size() - 1 - undoCounter);
	}
	
	public void clear()
	{
	
		
		undoCounter = 0;
		
		if(mBitmaps != null)
		for(int i = 0;i< mBitmaps.size(); i++)
			mBitmaps.get(i).recycle();
		
		mBitmaps = new ArrayList<Bitmap>();
		isUndoing = false;
	}
	
	public void undoing()
	{
		isUndoing = true;
	}
	
	// return true if I can perform undo
	public boolean isUndoValid(){
		return (undoCounter < mBitmaps.size() - 1);
	}
	
	// return true if I can perform redo
	public boolean isRedoValid(){
		return (undoCounter > 0);
	}
	
	public String saveBitmapToMemory(Bitmap bmp)
	{
		
	//	Log.e("CYY", Environment.getExternalStorageDirectory().toString());
		String root = Environment.getExternalStorageDirectory().toString();
		new File(root + "/pictures/CanvasNET").mkdirs();
		// String root = Environment.getExternalStorageDirectory().toString();
		// new File(root + "/CanvasNET").mkdirs();
		Bitmap bg = bmp.copy(Bitmap.Config.ARGB_8888, true);
		Canvas tempcanvasCanvas = new Canvas(bg);
		tempcanvasCanvas.drawColor(Color.WHITE);
		tempcanvasCanvas.drawBitmap(bmp, 0, 0, new Paint(Paint.DITHER_FLAG));
		
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		bg.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
		
		bg.recycle();
		tempcanvasCanvas = null;
   
		String fileName,fileNameBase = "CanvasNetPic" ;
		int counter =1;
		fileName = "CanvasNetPic0";
		// you can create a new file name "test.jpg" in sdcard folder.
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "pictures" + File.separator + "CanvasNET"
				+ File.separator +fileName + ".jpg");
		while(f.exists())
		{
			fileName = fileNameBase + Integer.toString(counter);
			 f = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "pictures" + File.separator + "CanvasNET"
					+ File.separator +fileName + ".jpg");
			 counter++;
		}
		
		
		
		try {
			f.createNewFile();

			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			bytes.close();
			fo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileName;
		
	}

}
