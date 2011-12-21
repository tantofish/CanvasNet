package ntu.csie.wcm;


import java.math.BigDecimal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
        }
        else
        {
        	String remoteIP = bundle.getString("IP");
        	mSocket.client(remoteIP, 5050);
        }
        

        
        
        
        
		
		ImageButton CcBtn;  //change color button
		CcBtn = (ImageButton) findViewById(R.id.ChangeColorBt);
		CcBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				useColorPicker();
			}
		});
		
		

		
		ImageButton eraserBtn;
		eraserBtn = (ImageButton) findViewById(R.id.EraserBt);
		eraserBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.getPaint().setColor(Color.WHITE);
			}
		});
		
		ImageButton undoBtn = (ImageButton) findViewById(R.id.undoBt);
		undoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				mView.undo();
			}
		});
		
		ImageButton redoBtn = (ImageButton) findViewById(R.id.redoBt);
		redoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.redo();
			}
		});
		
		ImageButton clearBtn = (ImageButton) findViewById(R.id.clearBt);
		clearBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				mView.clearCanvas(mSelf);
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
		menu.add(0, 0, 0, "next page");
		menu.add(0, 1, 1, "frame select");
		menu.add(0, 2, 2, "load image");
		menu.add(0, 3, 3, "check ip");
		menu.add(0, 4, 4, "disconnect");
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
			LayoutInflater inflater = LayoutInflater.from(MyCanvas.this);  
	        final View textEntryView = inflater.inflate(R.layout.dialog, null);  
	        final TextView ipTextView=(TextView)textEntryView.findViewById(R.id.ipTextView);
	        //String ip = "192.168.xxx.test";
	        
	        ipTextView.setText(mSocket.getIP());
	        final ProgressDialog.Builder builder = new ProgressDialog.Builder(MyCanvas.this); 
	        builder.setCancelable(false);  
	        builder.setTitle("教室位址");  
	        builder.setView(textEntryView);
	        builder.setNegativeButton("確定",  
	                new DialogInterface.OnClickListener() {  
	                    public void onClick(DialogInterface dialog, int whichButton) {  
	                        setTitle("");  
	                    }  
	                });  
	        builder.show();
			break;
		case 4:
			mSocket.disconnect();
			this.finish();
			break;
		case 5:
			Toast.makeText(MyCanvas.this, "中肯", Toast.LENGTH_SHORT).show();
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

	
 
    
    

}
