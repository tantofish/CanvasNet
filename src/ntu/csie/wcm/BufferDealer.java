/*this class handle about Bitmaps for undo/redo and save use.*/



package ntu.csie.wcm;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;


public class BufferDealer {
	private final int BITMAP_CACHE_SIZE = 3;
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

}
