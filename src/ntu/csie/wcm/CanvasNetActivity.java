package ntu.csie.wcm;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class CanvasNetActivity extends Activity {
	/** Called when the activity is first created. */



	ImageButton HostStartBtn;
	ImageButton ClientStartBtn;
	ImageButton mQRcodeBtn; 
	ImageButton mAboutBtn;
	ImageView mCover,mTitle;

	TransitionDrawable transition;
	ScrollView sview;
	LinearLayout llayout;
	Thread tmp,mAboutThread;
	int counter;
	int bottom;
	//Button imgLoaderActivityJumper; // tantofish:temporary use.
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation(1);	//lock rotate
		
		//ChengYan: show cover in transition animation
		 Resources res = getResources();
        transition = (TransitionDrawable) res.getDrawable(R.drawable.cover_transition);
        mCover = (ImageView)findViewById(R.id.cover);
        mCover.setImageDrawable(transition);
        transition.startTransition(1500);
        
        if(sview == null) Log.d("proj", "null!!!");
        startCoverThread(res);
       
		

        
        
		//ChengYan cover click listener
        mCover.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//	transition = null;
				
				mCover.setClickable(false);
				mCover.setVisibility(View.INVISIBLE);
			
			}
		});
        
        
        //ChengYan: title animation

        
        mTitle = (ImageView) findViewById(R.id.title);
        final Animation ani1 = AnimationUtils.loadAnimation(this, R.anim.title_canvasnet_animation);
        mTitle.startAnimation(ani1);
        mTitle.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
		        Animation ani2 = AnimationUtils.loadAnimation(CanvasNetActivity.this, R.anim.title_canvasnet_anim_onclick);
				mTitle.clearAnimation();
				ani2.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						mTitle.clearAnimation();
						mTitle.startAnimation(ani1);
					}
				});
				
				mTitle.startAnimation(ani2);
				return false;
			}
		});
        
        
        //ChengYan: QR code button
        mQRcodeBtn = (ImageButton)findViewById(R.id.QRcodeBtn);
        mQRcodeBtn.setAlpha(50);
        mQRcodeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 openQRCodeDialog();
			}
		});
        
        //Tantpfish: about us button (image is a tag)
        mAboutBtn = (ImageButton)findViewById(R.id.aboutBtn);
        
        //ChengYan: apply animation to about button
        final Animation aboutAni = AnimationUtils.loadAnimation(this, R.anim.about_animation);
        mAboutBtn.startAnimation(aboutAni);
        
        mAboutBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					
				}
				return false;
			}
		});
        

        //About us functionality
        mAboutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = LayoutInflater.from(CanvasNetActivity.this);
				View login_view = inflater.inflate(R.layout.aboutus,null);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(CanvasNetActivity.this);
				builder.setView(login_view);
				AlertDialog dialog = builder.create();
				
				dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						mAboutThread.interrupt();
						Log.e("CYY", Boolean.toString(mAboutThread.isInterrupted()));
						
					}
				});
				
				dialog.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						mAboutThread.interrupt();
						Log.e("CYY", "onCancel");
					}
				});
				
				
				
				dialog.show();
		
				
				sview = (ScrollView)login_view.findViewById(R.id.aboutusview);
				if(sview == null) Log.d("proj", "null!!!");
				llayout = (LinearLayout)login_view.findViewById(R.id.lLayout1);
				
				mAboutThread = new Thread(){
					public void run(){
						while(!interrupted()){
							try {								
								sleep(100);
								
								Log.e("CYY", "scrolling");
								
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										bottom = llayout.getHeight() - sview.getHeight();
										if(sview.getScrollY() >= bottom){
											counter = 0;
											sview.scrollTo(0, 0);
											return;
										}
										sview.smoothScrollBy(0, 5);	
									}
								});
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Thread.currentThread().interrupt();
							}
						}						
					}
				};
				mAboutThread.start();
				

			}
		});
        /////////////////////////
        
        //ChengYan: animation for on click button
        final Animation onclickBtnAnimation = AnimationUtils.loadAnimation(this, R.anim.click_btn_animation);
        
        
        
		HostStartBtn = (ImageButton) findViewById(R.id.hostBtn);
		
		HostStartBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//openDialog(new HostPositiveListener(),"Using Host",true);
				onClickHost();
			}
		});
		
		// tantofish : chenge button image when click
		HostStartBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					HostStartBtn.setImageResource(R.drawable.bt_host_down);
					HostStartBtn.startAnimation(onclickBtnAnimation);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					HostStartBtn.setImageResource(R.drawable.bt_host);
					//HostStartBtn.setImageDrawable(null);
				}
				return false;
			}
		});
		
		ClientStartBtn = (ImageButton) findViewById(R.id.clientBtn);

		ClientStartBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openDialog(new ClientPositiveListener(),"Using client, please input the MAGIC NUMBER",false);
			}
		});
		ClientStartBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					ClientStartBtn.setImageResource(R.drawable.bt_client_down);
					ClientStartBtn.startAnimation(onclickBtnAnimation);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					ClientStartBtn.setImageResource(R.drawable.bt_client);
					//ClientStartBtn.setImageDrawable(null);
				}
				return false;
			}
		});

		

	}
	
	
	// ChengYan: thread for cover animation
	private void startCoverThread(final Resources res) {

		Thread tmp = new Thread() {

			public void run() {
				// TODO Auto-generated method stub
				try {
					sleep(1600);

					runOnUiThread(new Runnable() {
						public void run() {

							transition.reverseTransition((1500));

						}
					});
					sleep(1800);
				    if(!mCover.isClickable())
				    	return;
					
					runOnUiThread(new Runnable() {
						public void run() {

							Animation fadeout = AnimationUtils.loadAnimation(CanvasNetActivity.this, R.anim.cover_fadeout);
							mCover.startAnimation(fadeout);
							mCover.setClickable(false);
							mCover.setVisibility(View.INVISIBLE);
							
							
							

						}
					});
					
					
				

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		tmp.start();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		this.finish();
	}



	class BasePositiveListener implements DialogInterface.OnClickListener {
    	public void onClick(DialogInterface dialog, int id) {}
	}

	class HostPositiveListener extends BasePositiveListener {
		@Override
		public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			super.onClick(dialog, id);
			
			 onClickHost();

		}
	}
	
	private void onClickHost()
	{
		Intent intent = new Intent();
		intent.setClass(CanvasNetActivity.this, MyCanvas.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isServer", true);
		intent.putExtras(bundle);
		
		startActivity(intent);
	}

	private EditText input;
	 private String IpCache;
	 
	 ProgressDialog connectngDialog;
	class ClientPositiveListener extends BasePositiveListener{
		@Override
		public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			super.onClick(dialog, id);
			
		//	connectngDialog = ProgressDialog.show(CanvasNetActivity.this, "", "Loading. Please wait...", true);
        	
			
			IpCache = input.getText().toString();
			
			if(IpCache.length() != 6)
			{
				Toast.makeText(getApplicationContext(), "Magic Number should be six numbers!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Intent intent = new Intent();
			intent.setClass(CanvasNetActivity.this, MyCanvas.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("isServer", false);
			bundle.putString("IP", IpCache);
			intent.putExtras(bundle);
			
			startActivity(intent);
			
			
			
		}
    }
	

	private void openDialog(BasePositiveListener l,String message,boolean isHost)
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
       
        builder.setMessage(message);
       // builder.setCancelable(false);
        if(!isHost)
        {
        	// Set an EditText view to get user input 
        	//if(input == null)
        	input = new EditText(this);
        	input.setInputType(0x00000014);
        	if(IpCache != null)
        		input.setText(IpCache);
        	builder.setView(input);
        	
        }
        builder.setPositiveButton("Yes", l);
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        	}
        });   
         
       
        AlertDialog alert = builder.create();
     
        alert.show();
		
	}
	
	private void openQRCodeDialog()
	{
		
		LayoutInflater inflater = LayoutInflater.from(CanvasNetActivity.this);  
        final View textEntryView = inflater.inflate(R.layout.qrcode_dialog, null);  
        final ProgressDialog.Builder dialog = new ProgressDialog.Builder(CanvasNetActivity.this); 
      //  dialog.setCancelable(false);  
        dialog.setTitle("Get Canvas.NET from Android Market!!");  
        dialog.setView(textEntryView);
        dialog.show();
		
	}
	
	private void gotoAboutUsActivity(){
		
	}
	
}
