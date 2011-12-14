package ntu.csie.wcm;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MySurfaceView extends View {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;
	
	private final int BITMAP_CACHE_SIZE = 10;

	private Paint mPaint;
	private ArrayList<Bitmap> mBitmaps;
	Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Context mContext;
	private int mWidth,mHeight;

	public MySurfaceView(Context c, AttributeSet attrs) {
		super(c, attrs);
		mContext = c;

		mBitmaps = new ArrayList<Bitmap>();

		// mPaint = MyCanvas.mPaint;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

	}

	public Paint getPaint()
	{
	 return mPaint;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mWidth = w;
		mHeight = h;
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mBitmaps.clear();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xFFAAAAAA);
		canvas.drawColor(Color.WHITE);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;// 4;

	private void touch_start(float x, float y) {

		// mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		
		mCanvas.drawPath(mPath, mPaint);
		undoCounter = 0;
		saveBitmap();
	
		mPath.reset();
	}
	
	
	private int undoCounter = 0;
	// function to save bitmap, so that undo can load it
	private void saveBitmap() {
		if (mBitmaps.size() > BITMAP_CACHE_SIZE)
			mBitmaps.remove(0);
		
		
		mBitmaps.add(Bitmap.createBitmap(mBitmap));
	}

	// undo function  e98877331:FIXME: undo then touch_up() will break
	public void undo() {
		
				
		if(undoCounter <mBitmaps.size()-1)
		{
			++undoCounter;
		mBitmap = mBitmaps.get(mBitmaps.size()-1-undoCounter);
	

		mCanvas = new Canvas(mBitmap);
		
		
		invalidate();
		}
		
		Log.e("test", Integer.toString(undoCounter) + " " + mBitmaps.size());
	}

	public void redo() {
		
		if (undoCounter > 0)
		{
			--undoCounter;
		mBitmap = mBitmaps.get(mBitmaps.size()-1-undoCounter);
		mCanvas = new Canvas(mBitmap);
		}
		
		invalidate();
	}
	 

    public void clearCanvas()
    { 
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mBitmaps.clear();    
		invalidate();
    }
    
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

}
