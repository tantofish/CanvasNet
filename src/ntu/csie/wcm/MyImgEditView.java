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
	
	private int mWidth, mHeight;	// window width and window height
	private int fWidth, fHeight;	// fitted width and fitted height 

	private Bitmap srcImg;
	// Starting values of each finger "move" 
	private float stCenterX;
	private float stCenterY;
	private float stAngle;
	private float stDistance;
	
	private float lastX;
	private float lastY;
	private float lastSVX;	// slope vector
	private float lastSVY;	// slope vector
	private float lastDistance;
	
	private float lastH; //!!
	private float lastW;
	
	
	private float thisX;
	private float thisY;
	private float thisSVX;	// slope vector
	private float thisSVY;	// slope vector
	private float thisDistance;
	
	
	
	
	private float stackX = 0.f;
	private float stackY = 0.f;
	private float stackAngle = 0.f;
	private float stackScale = 1.f;
	private RelativeLayout.LayoutParams stackParams;
	
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
		//mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = ImgLoaderActivity.computeSampleSize(opts, -1, mWidth*mHeight);
		opts.inJustDecodeBounds = false;
		try {
			srcImg = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError err) {
		}
		
		int w = srcImg.getWidth();
		int h = srcImg.getHeight();
		if( (w > mWidth) || (h > mHeight) ){
			if( (w/h) > (mWidth/mHeight) ){
				fWidth = mWidth;
				fHeight = mWidth * h / w;
			}else{
				fWidth = mHeight * w / h;
				fHeight = mHeight;
			}
		}else{
			fWidth  = w;
			fHeight = h;
		}
		
		
		
        params = new RelativeLayout.LayoutParams(fWidth,fHeight);
        lmParams = new RelativeLayout.LayoutParams(fWidth,fHeight);
        stackParams = new RelativeLayout.LayoutParams(fWidth,fHeight);
        params.rightMargin = 5000;
		params.bottomMargin =5000;
		
		lmParams.rightMargin = 5000;
		lmParams.bottomMargin = 5000;
		
		stackParams.rightMargin = 5000;
		stackParams.bottomMargin = 5000;

		lmAngle = 0.f;
		lmScale = 1.f;
		
		stackX = 0.f;
		stackY = 0.f;
		stackAngle = 0.f;
		stackScale = 1.f;
				
		
		
		((MyCanvas)mContext).setCanvasViewMode(MyCanvas.VIEWMODE_IMAGE_EDITING);
        ((MyCanvas)mContext).transformIV(lmAngle, lmParams, srcImg);
        
        
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		invalidate();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int pointerCount = event.getPointerCount();  // multitouch or single touch 
		/*Point offset = new Point();*/
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		
			lastX = event.getX();
			lastY = event.getY();
		
			break;
		case MotionEvent.ACTION_MOVE:
			
			pointerCount = event.getPointerCount();	// how many points touch on the screen
			switch (pointerCount){
			case 1:	// single point touch
				if(!isMultitouching){
					thisX = event.getX();
					thisY = event.getY();
					stackParams.leftMargin += thisX - lastX;
					stackParams.topMargin  += thisY - lastY;
					lastX = thisX;
					lastY = thisY;
					((MyCanvas)mContext).transformIV(stackAngle, stackParams, srcImg);
				}
				break;
			case 2:	// two points touch
				
				float x1 = event.getX(0);
				float y1 = event.getY(0);
				float x2 = event.getX(1);
				float y2 = event.getY(1);
				
				
				/*angle    = (float) Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
				if(angle<0) angle+=180;*/
				distance = (float) (Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2)));
				
				if(!isMultitouching){
					isMultitouching = true;
					
					lastSVX = x2 - x1;
					lastSVY = y2 - y1;
					
					
					lastDistance = distance;
					lastH = stackParams.height;
					lastW = stackParams.width;
					lastX = stackParams.leftMargin;
					lastY = stackParams.topMargin;
				}else{
					
					stackScale = distance/lastDistance;
					stackParams.topMargin  = (int) (lastY + lastH * (1.f-stackScale)/2);
					stackParams.leftMargin = (int) (lastX + lastW * (1.f-stackScale)/2);
					stackParams.height = ((int) (lastH * stackScale));
					stackParams.width  = ((int) (lastW * stackScale));
					
					thisSVX = x2 - x1;
					thisSVY = y2 - y1;
					double cosTheta = (lastSVX*thisSVX+lastSVY*thisSVY) / 
							          (Math.sqrt( lastSVX*lastSVX + lastSVY*lastSVY) * 
							           Math.sqrt( thisSVX*thisSVX + thisSVY*thisSVY));
					angle = (float) Math.toDegrees(Math.acos(cosTheta));
					
					Log.e("tantofish","("+x1+","+y1+")("+x2+","+y2+") angle = " + angle);
					stackAngle = angle;
					
					((MyCanvas)mContext).transformIV(stackAngle, stackParams, srcImg);
			       
				}

				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(isMultitouching){
				lmAngle += dAngle;
				lmScale *= scale;
				lmParams.topMargin = params.topMargin;
				lmParams.leftMargin = params.leftMargin; 
				lmParams.height = params.height; 
				lmParams.width  = params.width;
				isMultitouching = false;

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
        
        Bitmap rotatedBM = createBitmapCarefully(srcImg, matrix);
        
        
        int srcW = rotatedBM.getWidth() ;
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
        
		((MyCanvas)mContext).setCanvasViewMode(MyCanvas.VIEWMODE_CANVAS);
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

	public static Bitmap createBitmapCarefully(Bitmap srcImg, Matrix matrix) {
		Bitmap bmOK = null;
		try{
			bmOK = Bitmap.createBitmap(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), matrix, true);
        }catch(OutOfMemoryError err){
        	matrix.postScale(0.8f, 0.8f);
        	try{
        		bmOK = Bitmap.createBitmap(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), matrix, true);
            }catch(OutOfMemoryError err2){
            	matrix.postScale(0.8f, 0.8f);
            	try{
            		bmOK = Bitmap.createBitmap(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), matrix, true);
                }catch(OutOfMemoryError err3){
                	matrix.postScale(0.8f, 0.8f);
                	try{
                		bmOK = Bitmap.createBitmap(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), matrix, true);
                    }catch(OutOfMemoryError err4){
                    	
                    }
                }
            }
        }
	    return bmOK;
	}
}
