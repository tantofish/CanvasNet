package ntu.csie.wcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class ImgLoaderActivity extends Activity {
	/** Called when the activity is first created. */

	Gallery g_folder, g_photo;
	ImageView i1;
	Context context;
	int level = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("QQQQQQQQQQQQ","onCreate0");
		super.onCreate(savedInstanceState);
		Log.d("QQQQQQQQQQQQ","onCreate1");
		setContentView(R.layout.gallery);
		context = ImgLoaderActivity.this;
		g_folder = (Gallery) findViewById(R.id.gallery_folder);
		g_photo = (Gallery) findViewById(R.id.gallery1);
		i1 = (ImageView) findViewById(R.id.imageView1);
		Log.d("QQQQQQQQQQQQ","onCreate2");
		g_folder.setAdapter((SpinnerAdapter) new ImageAdapter(context));
		Log.d("QQQQQQQQQQQQ","onCreate3");
		g_folder.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				Toast.makeText(context, String.valueOf(position),
						Toast.LENGTH_SHORT).show();

				if (position == 0) {
					level = 11;
				} else if (position == 1) {
					level = 12;
				} else if (position == 2) {
					level = 13;
				}
				g_photo.setAdapter((SpinnerAdapter) new ImageAdapter(context));
				backgroundType(i1);
			}
		});

		g_photo.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				Toast.makeText(context, String.valueOf(position),
						Toast.LENGTH_SHORT).show();
				if (level == 11) {
					i1.setImageResource(new ImageAdapter(context).ImageId1[position]);
				} else if (level == 12) {
					i1.setImageResource(new ImageAdapter(context).ImageId2[position]);
				} else if (level == 13) {
					i1.setImageResource(new ImageAdapter(context).ImageId3[position]);
				}
				backgroundType(i1);
			}
		});
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			if (level == 1) {
				return folderImageIds.length;
			} else if (level == 11) {
				return ImageId1.length;
			} else if (level == 12) {
				return ImageId2.length;
			} else {
				return ImageId3.length;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			if (level == 1) {
				i.setImageResource(folderImageIds[position]);
			} else if (level == 11) {
				i.setImageResource(ImageId1[position]);
			} else if (level == 12) {
				i.setImageResource(ImageId2[position]);
			} else if (level == 13) {
				i.setImageResource(ImageId3[position]);
			}
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(120, 120));
			backgroundType(i);
			return i;
		}

		private Integer[] folderImageIds = { R.drawable.folder,
				R.drawable.folder, R.drawable.folder };
		private Integer[] ImageId1 = { R.drawable.pic_1, R.drawable.pic_2,
				R.drawable.pic_3};
		private Integer[] ImageId2 = { R.drawable.photo_1, R.drawable.photo_2,
				R.drawable.photo_3 };
		private Integer[] ImageId3 = { R.drawable.img_1, R.drawable.img_2,
				R.drawable.img_3, R.drawable.img_4};
	}

	/*
	 * 透過TypedArray設置Gallery/ImageView的背景風格
	 */
	public void backgroundType(ImageView image) {
		int mGalleryItemBackground;
		TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = a.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
		a.recycle();
		image.setBackgroundResource(mGalleryItemBackground);
	}
}