package ntu.csie.wcm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MyImgEditView extends View {

	//handler use 
	
    //public variable
	public Context mContext;

	//private variable
	public Bitmap mBitmap;
	public Canvas mCanvas;
	private Paint mBitmapPaint;
	private Paint mPaint;
	private Path  mPath;
	
	private int mWidth, mHeight;

	private Bitmap srcImg;
	
	// Starting values of each finger "move" 
	private float stCenterX;
	private float stCenterY;
	private float stAngle;
	private float stDistance;
	
	// the values tracked last move
	private float lmCenterX = 0;
	private float lmCenterY = 0;
	private float lmAngle = 0.f;
	private float lmScale = 1.f;
	private RelativeLayout.LayoutParams lmParams;
	
	// variables used in the current onTouch event
	private float x1, x2; 	// on multi touch x
	private float y1, y2;
	private boolean isMultitouching = false;
	private float centerX;
	private float centerY;
	private float angle;
	private float distance;
	private float scale;
	private float dAngle;
	private RelativeLayout.LayoutParams params;

	
	// tantofish: these are for a strange exception that rapidly consecutive click 
    //            at the OK button will cause consecutive pushBuffer() called.
	/*private boolean isON = false; 
	public boolean isOKBtnEnabled(){return isON;}
	public void setOKBtnEnable(boolean b){isON = b;}*/
	// bug fixed
	
	
	/* Constructor */
	public MyImgEditView(Context c, AttributeSet attrs) {
		super(c, attrs);
		mContext = c;	
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// tantofish: back ground color => (A,R,G,B) = 0x(55,88,88,88);
		canvas.drawColor(0x55888888);	
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	}

	public void startEditing(String path, Bitmap background){
		
		//mBitmap = background;
		
		Bitmap img = BitmapFactory.decodeFile(path);

		/* tantofish : Resize the image start here*/
		float marginX = 0.9f;
		float marginY = 0.8f;
		
		int width  = img.getWidth();
		int height = img.getHeight();
        int bm_w   = this.getWidth()  ;
        int bm_h   = this.getHeight() ;
        
        float scaleX = (float) bm_w * marginX / width;
        float scaleY = (float) bm_h * marginY / height;
        float scale = java.lang.Math.min(scaleX, scaleY);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        srcImg = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        img.recycle();
        
        params = new RelativeLayout.LayoutParams(srcImg.getWidth(),srcImg.getHeight());
        lmParams = new RelativeLayout.LayoutParams(srcImg.getWidth(),srcImg.getHeight());
        params.rightMargin = 2000;
		params.bottomMargin = 2000;
		lmParams.rightMargin = 2000;
		lmParams.bottomMargin = 2000;
		lmCenterX = 0;
		lmCenterY = 0;
		lmAngle = 0.f;
		lmScale = 1.f;
		
		
		((MyCanvas)mContext).setCanvasViewMode(MyCanvas.VIEWMODE_IMAGE_EDITING);
        ((MyCanvas)mContext).transformIV(lmAngle, lmParams, srcImg);
        
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		invalidate();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int pointerCount = event.getPointerCount();  // multitouch or single touch 
		Point offset = new Point();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			stCenterX = event.getX();
			stCenterY = event.getY();

			break;
		case MotionEvent.ACTION_MOVE:
			
			pointerCount = event.getPointerCount();	// how many points touch on the screen
			switch (pointerCount){
			case 1:	// single point touch
				
				if(isMultitouching) break;
				
				centerX = event.getX();
				centerY = event.getY();
				
				offset.set((int)(centerX-stCenterX), (int)(centerY-stCenterY));

				params.topMargin  = lmParams.topMargin + offset.y;
				params.leftMargin = lmParams.leftMargin + offset.x;
				((MyCanvas)mContext).transformIV(lmAngle, params, srcImg);
				
				break;
			case 2:	// two points touch
				
				x1 = event.getX(0);
				y1 = event.getY(0);
				x2 = event.getX(1);
				y2 = event.getY(1);
				centerX  = (float) ((x1 + x2) * 0.5);
				centerY  = (float) ((y1 + y2) * 0.5);
				angle    = (float) Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
				if(angle<0) angle+=180;
				distance = (float) (Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2))/2);
				
				if(!isMultitouching){
					isMultitouching = true;
					stCenterX = centerX;
					stCenterY = centerY;
					stAngle = angle;
					stDistance = distance;
					
				}else{
					
					scale = distance/stDistance;
					dAngle = angle-stAngle;
					int newH = (int) (lmParams.height * scale);
					int newW = (int) (lmParams.width * scale);
					params.topMargin  = lmParams.topMargin - ( newH - lmParams.height)/2;
					params.leftMargin = lmParams.leftMargin - (newW - lmParams.width)/2;
					params.height 	  = newH;
					params.width      = newW;
					
					((MyCanvas)mContext).transformIV(dAngle + lmAngle, params, srcImg);

			        			       
				}

				break;
				
			}
			break;
		case MotionEvent.ACTION_UP:
			if(isMultitouching){
				lmAngle += dAngle;
				//lmCenterX += centerX-stCenterX;
				//lmCenterY += centerY-stCenterY;
				lmScale *= scale;
				lmParams.topMargin = params.topMargin;
				lmParams.leftMargin = params.leftMargin; 
				lmParams.height = params.height; 
				lmParams.width  = params.width;
				isMultitouching = false;

			}else{
				lmCenterX += centerX-stCenterX;
				lmCenterY += centerY-stCenterY;
				lmParams.topMargin = params.topMargin;
				lmParams.leftMargin = params.leftMargin; 
			}

			
			break;
		}
		return true;
	}
	
	public void cancel(){
		//setVisibility(View.INVISIBLE);
		((MyCanvas)mContext).setCanvasViewMode(MyCanvas.VIEWMODE_CANVAS);
	}
	
	/* 
	 * Draw image onto the canvas when user load it from the gallery 
	 * ( which is stored in external storage)
	 */
	public Bitmap ok(Bitmap bm){
		
		Matrix matrix = new Matrix();
        matrix.postRotate(lmAngle);
       
        
        
        Bitmap rotatedBM = Bitmap.createBitmap(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), matrix, true);
        
        int srcW = rotatedBM.getWidth();
        int srcH = rotatedBM.getHeight();
        int adjW, adjH, offsetW, offsetH;
        if( lmParams.width > lmParams.height ){
        	adjH = lmParams.height;
        	adjW = (int)( adjH * srcW / srcH );
        }else{
        	adjW = lmParams.width;
        	adjH = (int)( adjW * srcH / srcW );
        }
        
        offsetW = (lmParams.width  - adjW) / 2;
        offsetH = (lmParams.height - adjH) / 2;
        
        Rect rect   = new Rect();
        rect.left   = lmParams.leftMargin + offsetW;			        
        rect.right  = rect.left + adjW;
        rect.top    = lmParams.topMargin  + offsetH;
        rect.bottom = rect.top + adjH;
        
        mBitmap = bm;
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(rotatedBM, null, rect, mBitmapPaint);
		invalidate();
        
		//((MyCanvas)mContext).setCanvasViewMode(MyCanvas.VIEWMODE_CANVAS);
		((MyCanvas)mContext).enableUndoDisableRedo();
		
		return mBitmap;
	}
	public Bitmap getBitmap(){
		return mBitmap;
	}
	public void setBitmap(Bitmap bm){
		mBitmap = bm;
	}
	public void errorToast(String str)
	{
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}


	

}
