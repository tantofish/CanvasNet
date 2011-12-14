package ntu.csie.wcm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.SurfaceHolder;

public class Canvas extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.canvaslayout);
		Log.e("hello","hihihih111111");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 把计1:竤舱id, 把计2:itemId, 把计3:item抖, 把计4:item嘿
		menu.add(0, 0, 0, "next page");
		menu.add(0, 1, 1, "frame select");
		menu.add(0, 2, 2, "load image");
		menu.add(0, 3, 3, "check ip");
		menu.add(0, 4, 4, "disconnect");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ㄌ沮itemIdㄓ耞ㄏノ翴匡item
		switch (item.getItemId()) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}
}
