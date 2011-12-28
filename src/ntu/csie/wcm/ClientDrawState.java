package ntu.csie.wcm;

import android.graphics.Paint;
import android.graphics.Path;

public class ClientDrawState {
	private Path mPath;
	private Paint mPaint;
	public float mX,mY;
	
	public ClientDrawState()
	{
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		mPath = new Path();
	}
	
	
    public Path getPath()
    {
    	return mPath;
    }
	
    
    public Paint getPaint()
    {
    	return mPaint;
    		
    }


}
