/*this class handle about Bitmaps for undo/redo and save use.*/



package ntu.csie.wcm;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;


public class BufferDealer {
	private final int BITMAP_CACHE_SIZE = 5;
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
				mBitmaps.remove(mBitmaps.size()-1);

			}
			isUndoing = false;
			undoCounter = 0;
		}
		
		saveBitmap(bmap);

	}
	
	public void saveBitmap(Bitmap bmap) {
		if (mBitmaps.size() > BITMAP_CACHE_SIZE)
			mBitmaps.remove(0);
		

		mBitmaps.add(Bitmap.createBitmap(bmap));


	}
	
	//get previous bitmap
	public Bitmap getP() {

		if (undoCounter < mBitmaps.size() - 1) {
			++undoCounter;
			

		}

        return mBitmaps.get(mBitmaps.size() - 1 - undoCounter);
	}

	//get next bitmap
	public Bitmap getN() {

		if (undoCounter > 0) {
			--undoCounter;
		}

		return mBitmaps.get(mBitmaps.size() - 1 - undoCounter);
	}
	
	public void clear()
	{
	
		
		undoCounter = 0;
		mBitmaps = new ArrayList<Bitmap>();
		isUndoing = false;
	}
	
	public void undoing()
	{
		isUndoing = true;
	}

}
