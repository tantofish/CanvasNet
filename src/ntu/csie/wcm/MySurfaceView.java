package ntu.csie.wcm;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	
	 SurfaceHolder holder;
	
	 MyThread thread;
	
	public MySurfaceView(Context context ,AttributeSet attrs) {
		super(context);
		
		// TODO Auto-generated constructor stub
        holder = this.getHolder();
        holder.addCallback(this);
        Log.e("hello","mysurfaceview create");
        thread = new MyThread();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		thread.doTouchEvent(event);
		return super.onTouchEvent(event);
		
	}

	
	
    class MyThread extends Thread{  
    	  
        @Override  
        public void run() {  
        	
        	
        	
            Canvas canvas = holder.lockCanvas(null);
            doDraw(canvas);

              
        }
        
        public void doTouchEvent(MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP) {

        

            } else {

        	int pc = event.getPointerCount();
        	 Log.e("hello","pc = " + pc);
        	if(pc > 1)
        	{
        		for(int i = 0; i< pc;i++)
        		Log.e("hello",event.getX(i) + "," + event.getY(i) + " pointCount: " + event.getPointerCount());
        	}
        	else
          Log.e("hello",event.getX() + "," + event.getY() + " pointCount: " + event.getPointerCount());
            }
        }
        
        private void doDraw(Canvas ca)
        {
        	
            Paint mPaint = new Paint();  
            mPaint.setColor(Color.BLUE);  
              
            ca.drawRect(new RectF(40,60,80,80), mPaint);  
            Log.e("hello","drawing");
            holder.unlockCanvasAndPost(ca); 
        }
     
    } 
}
