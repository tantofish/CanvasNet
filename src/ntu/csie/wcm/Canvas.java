package ntu.csie.wcm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
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
		menu.add(0, 0, 0, "毙");
		menu.add(0, 1, 1, "и");
		return super.onCreateOptionsMenu(menu);
	}
}
