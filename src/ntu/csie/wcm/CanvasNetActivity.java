package ntu.csie.wcm;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CanvasNetActivity extends Activity {
	/** Called when the activity is first created. */


	ImageButton HostStartBtn,ClientStartBtn; 
	ImageView mCover;
	TransitionDrawable transition;
	
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

       
        startCoverThread(res);
       
		

        
        
		//ChengYan cover click listener
        mCover.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//	transition = null;
				
				mCover.setClickable(false);
				mCover.setVisibility(View.INVISIBLE);
			
			}
		});
        
        

		HostStartBtn = (ImageButton) findViewById(R.id.hostBtn);
		
		HostStartBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				openDialog(new HostPositiveListener(),"Using Host",true);
			}
		});
		// tantofish : chenge button image when click
		HostStartBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					HostStartBtn.setImageResource(R.drawable.bt_host_down);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					HostStartBtn.setImageResource(R.drawable.bt_host);
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
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					ClientStartBtn.setImageResource(R.drawable.bt_client);
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
			
			
			Intent intent = new Intent();
			intent.setClass(CanvasNetActivity.this, MyCanvas.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("isServer", true);
			intent.putExtras(bundle);
			
			startActivity(intent);
		}
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
        builder.setCancelable(false);
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
	
	
}



