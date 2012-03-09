
/*
 * ChengYan: this class creates a ColorPickDialog when color pick button in MyCanvas is
 * touched.
 */

package ntu.csie.wcmlab;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ColorPickDialog extends Dialog {



  
    private int mInitialColor;
    private Paint mPaintToChange;
    private Context mContext;
    private SeekBar mAlphaSB;
    private SeekBar mWidthSB;
    private int mCurrentAlpha;
    
    private static class ColorPickerView extends View {
    	private Paint mPaintToChange;
        private Paint mPaint;
        private Paint mCenterPaint;
        private final int[] mColors;
        private ColorPickDialog mSelf;
       
    

        ColorPickerView(Context c, Paint paint, int color,ColorPickDialog self) {
            super(c);
            
            mSelf = self;
            mPaintToChange = paint;
            
            mColors = new int[] {
                0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,  Color.WHITE,Color.BLACK,0xFF00FFFF, 0xFF00FF00,
                0xFFFFFF00, 0xFFFF0000
            };
            Shader s = new SweepGradient(0, 0, mColors, null);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(60);//origional:32

            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5); 
        }

        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        public void paintAlphaChange(int alpha)
        {
        	mPaintToChange.setAlpha(alpha);
        	mPaint.setAlpha(alpha);
        	mCenterPaint.setAlpha(alpha);
        	invalidate();
        }
        
        
        @Override
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;

            canvas.translate(CENTER_X, CENTER_X);

            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
            
            
            //ChengYan: draw StrokeWidth Bar         
            canvas.drawLine(-r, r+STROKE_WIDTH, r, r+STROKE_WIDTH, mPaintToChange);

            if (mTrackingCenter) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);

                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                                  CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                                  mCenterPaint);

                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X*2, CENTER_Y*2 + STROKE_WIDTH);
        }

        private static final int CENTER_X = 120;
        private static final int CENTER_Y = 120;
        private static final int CENTER_RADIUS = 32;
        private static final int STROKE_WIDTH = 60;
/*
        private int floatToByte(float x) {
            int n = java.lang.Math.round(x);
            return n;
        }
        private int pinToByte(int n) {
            if (n < 0) {
                n = 0;
            } else if (n > 255) {
                n = 255;
            }
            return n;
        }
*/
        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
           // int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int a = mPaint.getAlpha();
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
           
        }

    /*    private int rotateColor(int color, float rad) {
            float deg = rad * 180 / 3.1415927f;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            ColorMatrix cm = new ColorMatrix();
            ColorMatrix tmp = new ColorMatrix();

            cm.setRGB2YUV();
            tmp.setRotate(0, deg);
            cm.postConcat(tmp);
            tmp.setYUV2RGB();
            cm.postConcat(tmp);

            final float[] a = cm.getArray();

            int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
            int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

            return Color.argb(Color.alpha(color), pinToByte(ir),
                              pinToByte(ig), pinToByte(ib));
        }*/

        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

            switch (event.getAction()) {
        
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        float angle = (float)java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*PI);
                        if (unit < 0) {
                            unit += 1;
                        }
                        mCenterPaint.setColor(interpColor(mColors, unit));
                        mPaintToChange.setColor(interpColor(mColors, unit));
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            colorChanged(mCenterPaint.getColor());
                            
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
        
        public void colorChanged(int color) {
        	mPaintToChange.setColor(color);
        	//send to remote
            ((MyCanvas)(mSelf.mContext)).getSocket().send(new Commands.ChangeColorCmd(color,mPaintToChange.getStrokeWidth()));
        	mSelf.dismiss();
        }
    }

    public ColorPickDialog(Context context,
                             Paint paint,
                             int initialColor) {
        super(context);

        
        mPaintToChange = paint;
        mCurrentAlpha = paint.getAlpha();
        mInitialColor = initialColor;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final ColorPickerView cpv = new ColorPickerView(getContext(), mPaintToChange, mInitialColor,this);
        
        //change all paints alpha value in ColorPickerView
        cpv.paintAlphaChange(mCurrentAlpha);
        
        //create Alpha seek bar
        mAlphaSB = new SeekBar(mContext);
       
        mAlphaSB.setProgress((int)(mAlphaSB.getMax() * ((float)mCurrentAlpha/255)));     
        mAlphaSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int alpha = (int)((float)255 *((float)progress/(float)seekBar.getMax()));
				mCurrentAlpha = alpha;
				cpv.paintAlphaChange(mCurrentAlpha);
				
				
			}
		});
        
        //create Stroke Width seek bar
        mWidthSB = new SeekBar(mContext);
        
        mWidthSB.setProgress((int)(mWidthSB.getMax() * ((float)(mPaintToChange.getStrokeWidth())/ColorPickerView.STROKE_WIDTH)));     
        mWidthSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int swidth = (int)((float)ColorPickerView.STROKE_WIDTH *((float)progress/(float)seekBar.getMax()));
				
				cpv.mPaintToChange.setStrokeWidth(swidth);
				cpv.invalidate();
				
				
			}
		});
        
        
        //create dialog layout
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        //add ColorPickerView
        ll.addView(cpv);
        //add alpha text
        TextView atext = new TextView(mContext);
        atext.setText("Set Alpha");
        ll.addView(atext);
        //add alpha seek bar
        ll.addView(mAlphaSB);
        
        //add stroke width text
        TextView stext = new TextView(mContext);
        stext.setText("Set Brush Width");
        ll.addView(stext);
        //add stroke width bar
        ll.addView(mWidthSB);

        
        setContentView(ll);
      
        setTitle("Pick a Color");
    }
    
    
}