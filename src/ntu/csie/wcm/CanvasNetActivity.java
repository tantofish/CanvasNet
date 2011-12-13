package ntu.csie.wcm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CanvasNetActivity extends Activity {
	/** Called when the activity is first created. */

	Button temp; // e98877331:temporary use.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		temp = (Button) findViewById(R.id.button1);
		
		
		temp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(CanvasNetActivity.this, Canvas.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});
	}
}