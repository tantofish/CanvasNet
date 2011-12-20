package ntu.csie.wcm;

import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

	
    //public variable
	public Context mContext;
	
	
	//private variable
	private BufferDealer mBufferDealer;
	private MySocket mMySocket;
	private Paint mPaint;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private int mWidth, mHeight;

	public MySurfaceView(Context c, AttributeSet attrs) {
		super(c, attrs);

		mContext = c;

		mBufferDealer = new BufferDealer();
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

	public void setSocket(MySocket ms)
	{
		mMySocket = ms;
	}
	public Paint getPaint() {
		return mPaint;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBufferDealer.clear();
		
		mWidth = w;
		mHeight = h;
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mBufferDealer.saveBitmap(Bitmap.createBitmap(mBitmap));
	
		mCanvas = new Canvas(mBitmap);

		
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
		
		//save current bitmap
		mBufferDealer.onTouchStep(Bitmap.createBitmap(mBitmap),mCanvas);


		mPath.reset();
	}

	private int undoCounter = 0;



	// undo function 
	public void undo() {
			mBitmap = Bitmap.createBitmap(mBufferDealer.getP());
			mBufferDealer.undoing();
			//mCanvas = new Canvas(mBitmap);
			mCanvas = new Canvas(mBitmap);

			invalidate();


	}

	public void redo() {
		mBitmap = Bitmap.createBitmap(mBufferDealer.getN());
		mCanvas = new Canvas(mBitmap);
			invalidate();
	}
	
	public void testBGImg(Bitmap img) {	//tantofish: pass selected image from external storage

		float marginX = 0.9f;
		float marginY = 0.8f;
		
		int width  = img.getWidth();
		int height = img.getHeight();
        int bm_w   = mBitmap.getWidth()  ;
        int bm_h   = mBitmap.getHeight() ;
        
        float scaleX = (float) bm_w * marginX / width;
        float scaleY = (float) bm_h * marginY / height;
        
        float scale = java.lang.Math.min(scaleX, scaleY);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledImg = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        img.recycle();
        
        width  = scaledImg.getWidth();
        height = scaledImg.getHeight();
        
        int xOffset = (bm_w - width)/2;
        int yOffset = (bm_h - height)/2;
        		
			for(int j = 0 ; j < height ; j++)
				for(int i = 0 ; i < width ; i++)
					mBitmap.setPixel(i+xOffset, j+yOffset, scaledImg.getPixel(i, j));
			mCanvas = new Canvas(mBitmap);
		
			
			invalidate();
		

		Log.d("test", "bit map" + scaledImg.getHeight() + scaledImg.getWidth());
	}
	
	

	public void clearCanvas(MyCanvas mc) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(mc);
        
        builder.setMessage("Clear Canvas?");
        builder.setCancelable(false);
        
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        		mBufferDealer.clear();
        		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        		mBufferDealer.saveBitmap(Bitmap.createBitmap(mBitmap));
        		
        		mCanvas = new Canvas(mBitmap);
        		
        		invalidate();
        	}
        });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        	}
        });   
        
        AlertDialog alert = builder.create();
        alert.show();

		
		
		
		

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMySocket.send(new Commands.SendPointCmd(x, y, 1)); 	
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mMySocket.send(new Commands.SendPointCmd(x, y, 2)); 
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		mMySocket.send(new Commands.SendPointCmd(x, y, 3)); 
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
	
	
	public void process(Commands.BaseCmd cmd)
	{
		switch (cmd.ID)
		{
		case 1:

			Commands.SendPointCmd Dpc = (Commands.SendPointCmd) cmd;
			if(Dpc.getType() == 1)
			{
				touch_start(Dpc.getX(),Dpc.getY());
			}
			else if(Dpc.getType() == 2)
			{
				touch_move(Dpc.getX(),Dpc.getY());
			}
			else if(Dpc.getType() == 3)
			{
				touch_up();
			}
			//mPath.reset();
			invalidate();
		break;
		case 2:
		    Commands.SendNumberCmd Snc = (Commands.SendNumberCmd) cmd;
			Log.e("receive num", Integer.toString(Snc.getNum()));
			Log.e("receive num2", Integer.toString(Snc.getNum()));
			Log.e("receive num3", Integer.toString(Snc.getNum()));

			
		}
	}


}
