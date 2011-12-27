package ntu.csie.wcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class CanvasNetActivity extends Activity {
	/** Called when the activity is first created. */


	ImageButton HostStartBtn,ClientStartBtn; 
	//Button imgLoaderActivityJumper; // tantofish:temporary use.

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


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
				openDialog(new ClientPositiveListener(),"Using client, please input IP",false);
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

		
		
		/*
		imgLoaderActivityJumper
		     = (Button) findViewById(R.id.button2);
		imgLoaderActivityJumper.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(CanvasNetActivity.this, ImgLoaderActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				
				startActivity(intent);
			}

		});*/

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
	class ClientPositiveListener extends BasePositiveListener{
		@Override
		public void onClick(DialogInterface dialog, int id) {
			// TODO Auto-generated method stub
			super.onClick(dialog, id);
			IpCache = input.getText().toString();
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



