package ntu.csie.wcm;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MyCanvas extends Activity{

	private MyCanvas mSelf;
	private MySurfaceView mView;
	private MySocket mSocket;
	
	boolean mIsServer;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.canvaslayout);
		
		mSelf = this;
		mView = (MySurfaceView)findViewById(R.id.mySurfaceView1);
		
        mSocket  =new MySocket(mView , 5050, (WifiManager) getSystemService(WIFI_SERVICE));
        
        mView.setSocket(mSocket);
        
        Bundle bundle = this.getIntent().getExtras(); 
  	    
        mIsServer = bundle.getBoolean("isServer");
        if(mIsServer)
        {
        	mSocket.server();
        	checkIP();
        }
        else
        {
        	String remoteIP = bundle.getString("IP");
        	mSocket.client(remoteIP, 5050);
        }
        
        
		
		final ImageButton CcBtn;  //change color button
		CcBtn = (ImageButton) findViewById(R.id.ChangeColorBt);
		CcBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				useColorPicker();
			}
		});
		CcBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					CcBtn.setImageResource(R.drawable.bt_palette_down_128);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					CcBtn.setImageResource(R.drawable.bt_palette_128);
				}
				return false;
			}
		});
		

		
		final ImageButton eraserBtn;
		eraserBtn = (ImageButton) findViewById(R.id.EraserBt);
		eraserBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.getPaint().setColor(Color.WHITE);
			}
		});
		eraserBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					eraserBtn.setImageResource(R.drawable.bt_eraser_down_128);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					eraserBtn.setImageResource(R.drawable.bt_eraser_128);
				}
				return false;
			}
		});
		
		final ImageButton undoBtn = (ImageButton) findViewById(R.id.undoBt);
		undoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				mView.undo();
			}
		});
		
		undoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					undoBtn.setImageResource(R.drawable.bt_undo_down_128);
					PorterDuffColorFilter cf = new PorterDuffColorFilter(
							   Color.argb(200, 0, 200, 0), PorterDuff.Mode.SRC_ATOP
							);
					undoBtn.setColorFilter(cf);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					undoBtn.setImageResource(R.drawable.bt_undo_128);
				}
				return false;
			}
		});
		
		final ImageButton redoBtn = (ImageButton) findViewById(R.id.redoBt);
		redoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.redo();
			}
		});
		redoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					redoBtn.setImageResource(R.drawable.bt_redo_down_128);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					redoBtn.setImageResource(R.drawable.bt_redo_128);
				}
				return false;
			}
		});
		
		final ImageButton clearBtn = (ImageButton) findViewById(R.id.clearBt);
		clearBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.clearCanvas(mSelf);
			}
		});
		clearBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					clearBtn.setImageResource(R.drawable.bt_clear_down_128);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					clearBtn.setImageResource(R.drawable.bt_clear_128);
				}
				return false;
			}
		});
		
		
	}

	
	
	private void useColorPicker()
	{
		new ColorPickDialog(this , mView.getPaint() , mView.getPaint().getColor()).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
		menu.add(0, 0, 0, "Next Page");
		menu.add(0, 1, 1, "Frame Select");
		menu.add(0, 2, 2, "Load Image");
		menu.add(0, 3, 3, "Check IP");
		menu.add(0, 4, 4, "Disconnect");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 依據itemId來判斷使用者點選哪一個item
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(MyCanvas.this, "還沒做", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			Toast.makeText(MyCanvas.this, "還沒做", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			Intent intent = new Intent();
			intent.setClass(MyCanvas.this, ImgLoaderActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
			break;
		case 3:
			checkIP();
			break;
		case 4:
			mSocket.disconnect();
			this.finish();
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d("Debug","onActivityResult: 從 ImageLoader 回來了");
		super.onActivityResult(requestCode, resultCode, data);
		//facebook.authorizeCallback(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK){
			Bundle b = data.getExtras();
			String path = b.getString("imgPath");
			
			Log.d("Debug","Decode Returned Bitmap");
			Bitmap img = BitmapFactory.decodeFile(path);
			mView.testBGImg(img);
			
			
		}
	}
	
	public void checkIP(){

		// tantofish : I use a layout "dialog.xml" because I want to set the text size,
		//             and I have no idea how to achieve this without using an extra xml.
		LayoutInflater inflater = LayoutInflater.from(MyCanvas.this);  
        final View textEntryView = inflater.inflate(R.layout.dialog, null);  
        final TextView ipTextView=(TextView)textEntryView.findViewById(R.id.ipTextView);
        String ip = mSocket.getIP();
        ipTextView.setText(ip.subSequence(1, ip.length()));
        final ProgressDialog.Builder dialog = new ProgressDialog.Builder(MyCanvas.this); 
        dialog.setCancelable(false);  
        //dialog.setTitle("IP address");  
        dialog.setView(textEntryView);
        dialog.setNegativeButton("OK",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });  
        dialog.show();
	}

	
 
    
    

}
