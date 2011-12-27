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
	 public static final int GET_SHOW_TOAST = 9528;
	
	
    //public variable
	public Context mContext;
	
	
	//private variable
	private BufferDealer mBufferDealer;
	private MySocket mMySocket;
	private Paint mPaint;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	//ChengYan: mRemotePath for remote drawing action
	private Path mPath,mRemotePath;
	private Paint mBitmapPaint;
	private int mWidth, mHeight;

	// tantofish : test for multitouch
	private Bitmap previewBitmap;
	private Path previewPath;
	private boolean isMultitouching = false;
	
	//ChengYan: Hander for receive message from socket thread

    public Handler handler = new Handler() 
    {

        public void handleMessage(Message msg) 
        {
   	    switch (msg.what) 
	    {
   	        //ChengYan: receive command from socket thread
	        case GET_COMMAND:
	        	Bundle tempB = msg.getData();
	        	process((Commands.BaseCmd)tempB.getSerializable("cmd"));
		    break;
		    //ChengYan: show Toast when other threads needs
	        case GET_SHOW_TOAST:
	        	Bundle tempB1 = msg.getData();
	        	errorToast(tempB1.getString("message"));
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
		//path for receive from remote
		mRemotePath = new Path();
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
		
		//ChengYan: draw local path
		canvas.drawPath(mPath, mPaint);
		//ChengYan: draw remote path
		canvas.drawPath(mRemotePath, mPaint);

	

		
	}
    //ChengYan: temporary initialize, array number should depend on client number
	private float[] mX = {0,0}, mY = {0,0};
	private static final float TOUCH_TOLERANCE = 4;// 4;

	//ChengYan: pNumber indicates which mX,mY to use (local or remote#?)
	private void touch_start(float x, float y, Path p,int pNumber) {

		// mPath.reset();
		p.moveTo(x, y);
		mX[pNumber] = x;
		mY[pNumber] = y;
	}

	private void touch_move(float x, float y,Path p,int pNumber) {
		float dx = Math.abs(x - mX[pNumber]);
		float dy = Math.abs(y - mY[pNumber]);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			p.quadTo(mX[pNumber], mY[pNumber], (x + mX[pNumber]) / 2, (y + mY[pNumber]) / 2);
			mX[pNumber] = x;
			mY[pNumber] = y;
		}
	}

	private void touch_up(Path p,int pNumber) {
		p.lineTo(mX[pNumber], mY[pNumber]);

		mCanvas.drawPath(p, mPaint);
		
		undoCounter = 0;
		
		//ChengYan: save current bitmap
		mBufferDealer.onTouchStep(Bitmap.createBitmap(mBitmap),mCanvas);
		
		p.reset();
	}

	/* Undo function */ 
	private int undoCounter = 0;

	/* ChengYan: Undo function */ 
	public void undo() {
			mBitmap = Bitmap.createBitmap(mBufferDealer.getP());
			mBufferDealer.undoing();
			//mCanvas = new Canvas(mBitmap);
			mCanvas = new Canvas(mBitmap);

			
			invalidate();


	}

	/* ChengYan: Redo function */
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

	}
	

	//ChengYan: pop dialog to confirm the action 
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
			touch_start(x, y,mPath,0); //ChengYan: pNumber = 0 means use mPath's mX,mY
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mMySocket.send(new Commands.SendPointCmd(x, y, 2)); 
			touch_move(x, y,mPath,0);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mMySocket.send(new Commands.SendPointCmd(x, y, 3)); 
			touch_up(mPath,0);
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
		
		Log.e("receive command", Integer.toString(cmd.ID));
		
		switch (cmd.ID)
		{
		
		//ChengYan: Receive onTouch command
		case 1:
			
			Commands.SendPointCmd Dpc = (Commands.SendPointCmd) cmd;
	
			if(Dpc.getType() == 1)
			{
				touch_start(Dpc.getX(),Dpc.getY(),mRemotePath,1); //ChengYan: pNumber = 1 means use remotePath#1's mX,mY 
			}
			else if(Dpc.getType() == 2)
			{
				touch_move(Dpc.getX(),Dpc.getY(),mRemotePath,1);
			}
			else if(Dpc.getType() == 3)
			{
				touch_up(mRemotePath,1);
			}
			//mPath.reset();
			invalidate();
			
		//	*/
		break; 
		//ChengYan: Receive command which is added for debug
		case 2:
		    Commands.SendNumberCmd Snc = (Commands.SendNumberCmd) cmd;
			Log.e("receive num", Integer.toString(Snc.getNum()));
			break;
			
		//ChengYan: Receive change color and brush width command 
		case 3:
			Commands.ChangeColorCmd CCC = (Commands.ChangeColorCmd) cmd;
			mPaint.setColor(CCC.getColor());
			mPaint.setStrokeWidth(CCC.getWidth());

			break;
		
		//ChengYan: Receive clear command
		case 4:
            DoClearCanvas();
            break;
		//ChengYan: Receive UndoOrRedo Command
		case 5:
			Commands.UndoRedoCmd URC = (Commands.UndoRedoCmd) cmd;
			
			Log.e("Comamnd", "receive undo redo");
			if(URC.getUnOrRe())
				undo();
			else
				redo();
			break;
		//ChengYan: Receive BitMap	
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
