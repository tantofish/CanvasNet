package ntu.csie.wcm;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Object;
import java.util.Vector;

import android.net.Uri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

	Gallery g_folder;
	Gallery g_photo;
	Context context;
	ImageView image;
	
	Vector<File[]> files;
	int folderIndex = -1;
	int imageIndex = -1;
	int w, h;	//
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		
		context  = ImgLoaderActivity.this;
		g_folder = (Gallery)   findViewById(R.id.gallery_folder);
		g_photo  = (Gallery)   findViewById(R.id.gallery1);
		image	 = (ImageView) findViewById(R.id.imageView1);
		image.setImageBitmap(null);
		readExternalStoragePublicPicture();
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
		h = dm.heightPixels;
		
		g_folder.setAdapter((SpinnerAdapter) new ImageAdapter(context));
		
		g_folder.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				folderIndex = position;
				imageIndex = 0;
				g_photo.setAdapter((SpinnerAdapter) new ImageAdapter(context));
			}
		});

		g_photo.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				imageIndex = position;
				//image.setImageBitmap(BitmapFactory.decodeFile(files.get(folderIndex)[imageIndex].getPath()));
				
				Bitmap bm = BitmapFactory.decodeFile(files.get(folderIndex)[position].getPath());
				//記憶體會爆 所以要縮圖 
				int width = bm.getWidth();
				int height = bm.getHeight();
	            float scale = (float) w / width;
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap img = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
                bm.recycle();
				image.setImageBitmap(img);
				
				backgroundType(image);
			}
		});
		
		
		image.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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
			if (folderIndex == -1)
				return files.size();
			else
				return files.get(folderIndex).length;
		}

		public Object getItem(int position) {	return position;	}

		public long getItemId(int position) {	return position;	}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			if (imageIndex == -1)
				i.setImageResource(folderImageId);
			else{
				
				Bitmap bm = BitmapFactory.decodeFile(files.get(folderIndex)[position].getPath());
				if (bm != null){
					//記憶體會爆 所以要縮圖 這裡會導致滾動lag
					int width = bm.getWidth();
					int height = bm.getHeight();
		            int newWidth = w/5;
		            float scale = (float) newWidth / width;
	                Matrix matrix = new Matrix();
	                matrix.postScale(scale, scale);
	                Bitmap img = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	                bm.recycle();
					i.setImageBitmap(img);
				}else{
					return i;
				}
			}
			
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(120, 120));
			backgroundType(i);
			return i;
		}

		private Integer folderImageId = R.drawable.folder;
		
	}

	/*
	 * 透過TypedArray設置Gallery/ImageView的背景風格
	 */
	public void backgroundType(ImageView image) {
		int mGalleryItemBackground;
		TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
		a.recycle();
		image.setBackgroundResource(mGalleryItemBackground);
	}
	
	
	public void readExternalStoragePublicPicture() {
		/* Get the folder path: "mnt/sdcard/Pictures" */
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		/* Two filters that can tell if the file is a directory or a file */
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {	return file.isFile();	}
		};
		FileFilter dirFilter = new FileFilter() {
		    public boolean accept(File file) {	return file.isDirectory();	}
		};
		
		/* Get all the directorys in the path */
		File[] dirs = path.listFiles(dirFilter);
		files = new Vector<File[]>();
		
		/* Get all files in those directorys above */
		File [] f = path.listFiles(fileFilter);
		if(f.length>0) files.add(f);	// block those directory who has no files inside from being shown
		
		for(int i = 0 ; i < dirs.length ; i++){
			File newpath = new File(path.toString(), dirs[i].getName());
			f = newpath.listFiles(fileFilter);
			if(f.length>0) files.add(f);
		}
		
		
		File DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File newpath = new File(DCIM.toString(), "Camera");
		f = newpath.listFiles(fileFilter);
		if(f.length>0) files.add(f);
		
		/* Debug Message */
		for(int i = 0 ; i < dirs.length; i++){
			f = files.get(i);
			for(int j = 0 ; j < f.length ; j++){
				Log.d("DEBUG_GET_FILE", "DIR " + f[j].getPath());
			}
		}
	}
	/*public void createExternalStoragePublicPicture() {
	    
	    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File file = new File(path, "pic_1.png");
	    
	    try {
	    	path.mkdirs();
	        InputStream is = getResources().openRawResource(R.drawable.folder);
	        OutputStream os = new FileOutputStream(file);
	        byte[] data = new byte[is.available()];
	        is.read(data);
	        os.write(data);
	        is.close();
	        os.close();

	        MediaScannerConnection.scanFile(this,
	                new String[] { file.toString() }, null,
	                new MediaScannerConnection.OnScanCompletedListener() {
	            public void onScanCompleted(String path, Uri uri) {
	                Log.i("ExternalStorage", "Scanned " + path + ":");
	                Log.i("ExternalStorage", "-> uri=" + uri);
	            }
	        });
	    } catch (IOException e) {
	        
	        Log.w("ExternalStorage", "Error writing " + file, e);
	    }
	}*/
}