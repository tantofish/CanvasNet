package ntu.csie.wcm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MySurfaceView extends View {

	//handler use 
	 public static final int GET_COMMAND = 9527; 
	
	
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

	
	// tantofish : test for multitouch
	private Bitmap previewBitmap;
	private Path previewPath;
	private boolean isMultitouching = false;
	
	
	
    public Handler handler = new Handler() 
    {

        public void handleMessage(Message msg) 
        {
   	    switch (msg.what) 
	    {
	        case GET_COMMAND:
	        	Bundle tempB = msg.getData();
	        	process((Commands.BaseCmd)tempB.getSerializable("cmd"));
		    break;

            }
	    super.handleMessage(msg);
        }

    };
    
	
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
	
	public MySocket getSocket()
	{
		return mMySocket;
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
		
		//save current bitmap
		mBufferDealer.onTouchStep(Bitmap.createBitmap(mBitmap),mCanvas);
		
		mPath.reset();
	}

	/* Undo function */ 
	public void undo() {
			mBitmap = Bitmap.createBitmap(mBufferDealer.getP());
			mBufferDealer.undoing();
			//mCanvas = new Canvas(mBitmap);
			mCanvas = new Canvas(mBitmap);

			
			invalidate();


	}

	/* Redo function */
	public void redo() {
		mBitmap = Bitmap.createBitmap(mBufferDealer.getN());
		mCanvas = new Canvas(mBitmap);

		invalidate();
	}
	
	/* 
	 * Draw image onto the canvas when user load it from the gallery 
	 * ( which is stored in external storage)
	 */
	public void drawImgOntoCanvas(Bitmap img) {	

		
        
		
		int width  = img.getWidth();
        int height = img.getHeight();
        
      
        		
		for(int j = 0 ; j < height ; j++)
			for(int i = 0 ; i < width ; i++)
				mBitmap.setPixel(i, j, img.getPixel(i, j));
		mCanvas = new Canvas(mBitmap);
		
		invalidate();
		

		//Log.d("test", "bit map" + scaledImg.getHeight() + scaledImg.getWidth());
	}
	
	/*
	 * use alert dialog to ask whether the user decides to confirm the move or not. 
	 */
	public void clearCanvas() { 
		
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        
        builder.setMessage("Clear Canvas?");
        builder.setCancelable(false);
        
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        		DoClearCanvas();
        		mMySocket.send(new Commands.ClearCanvasCmd());
        	}
        });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        	}
        });   
        
        AlertDialog alert = builder.create();
        alert.show();
		
	}
	
	/* Actually clear the canvas */
	public void DoClearCanvas()
	{
		mBufferDealer.clear();
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mBufferDealer.saveBitmap(Bitmap.createBitmap(mBitmap));
		
		mCanvas = new Canvas(mBitmap);
		
		invalidate();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int pointerCount = event.getPointerCount();  // multitouch or single touch 
		
		
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMySocket.send(new Commands.SendPointCmd(x, y, 1)); 	
			touch_start(x, y);
			
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			
			pointerCount = event.getPointerCount();	// how many points touch on the screen
			switch (pointerCount){
			case 1:	// single point touch
				mMySocket.send(new Commands.SendPointCmd(x, y, 2)); 
				touch_move(x, y);
				invalidate();
				break;
			case 2:	// two points touch
				int x1 = (int) event.getX(0);  // first touch point
				int y1 = (int) event.getY(0);
				int x2 = (int) event.getX(1);  // second touch point
				int y2 = (int) event.getY(1);
				
				
				// tantofish : draw circle for test multitouch
				int centerX = (int) ((x1 + x2) * 0.5);
				int centerY = (int) ((y1 + y2) * 0.5);
				int radius = (int)(Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2))/2);

				if(!isMultitouching){
					isMultitouching = true;
					previewBitmap = Bitmap.createBitmap(mBitmap);	
				}else{
					mBitmap = Bitmap.createBitmap(previewBitmap);	
				}
				
				mCanvas = new Canvas(mBitmap);
				mCanvas.drawCircle( centerX, centerY, radius, mPaint );
				
				invalidate();
				
				
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			mMySocket.send(new Commands.SendPointCmd(x, y, 3)); 
			touch_up();
			invalidate();
			
			if(isMultitouching) isMultitouching = false;
			
			((MyCanvas)mContext).enableUndoDisableRedo();
			
			break;
		}
		
		return true;
	}
	
	/* tantofish start */
	// tantofish: mySurfaceView will need to tell MyCanvas that if the undo/redo button should be gray
	public boolean IcanRedo(){
		return mBufferDealer.isRedoValid();
	}
	public boolean IcanUndo(){
		return mBufferDealer.isUndoValid();
	}
	public Bitmap getBitmap(){
		return mBitmap;
	}
	public void setBitmap(Bitmap bm){
		mBitmap = bm;
		mCanvas = new Canvas(mBitmap);

		invalidate();
	}
	/* tantofish end */
	
	public void errorToast(String str)
	{
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}
	
	public void process(Commands.BaseCmd cmd)
	{
		
		switch (cmd.ID)
		{
		
		//Receive onTouch command
		case 1:

			
			
			Commands.SendPointCmd Dpc = (Commands.SendPointCmd) cmd;
			//Log.e("receive num", Float.toString(Dpc.getX()) + "," + Float.toString(Dpc.getY()));
			
			
			
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
			
		//	*/
		break; 
		//Receive command which is added for debug
		case 2:
		    Commands.SendNumberCmd Snc = (Commands.SendNumberCmd) cmd;
			Log.e("receive num", Integer.toString(Snc.getNum()));
			break;
			
		//Receive change color command 
		case 3:
			Commands.ChangeColorCmd CCC = (Commands.ChangeColorCmd) cmd;
			mPaint.setColor(CCC.getColor());

			break;
		
		//Receive clear command
		case 4:
            DoClearCanvas();
            break;
		//Recieve UndoOrRedo Command
		case 5:
			Commands.UndoRedoCmd URC = (Commands.UndoRedoCmd) cmd;
			
			Log.e("Comamnd", "receive undo redo");
			if(URC.getUnOrRe())
				undo();
			else
				redo();
			break;
		
		case 6:
			Commands.SendBitmapCommit SBC = (Commands.SendBitmapCommit) cmd;
			Bitmap tempBmp = BitmapFactory.decodeByteArray(SBC.getBytearray(), 0, SBC.getBytearray().length);
			drawImgOntoCanvas(tempBmp);
			tempBmp.recycle();
			Log.e("Comamnd", "receive bitmap");
			break;
			

			
		}
	}
}
