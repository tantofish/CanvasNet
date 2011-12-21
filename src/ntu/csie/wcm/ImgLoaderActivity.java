package ntu.csie.wcm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.lang.Object;
import java.util.Vector;



import android.app.Activity;
import android.content.*;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ImgLoaderActivity extends Activity {
	/** Called when the activity is first created. */

	Gallery g_folder;
	Gallery g_photo;
	Context context;
	ImageView image;
	
	
	Vector<File[]> files;
	int folderIndex = -1;
	int imageIndex = -1;
	int w, h;
	String pathString;
	
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
				pathString = files.get(folderIndex)[position].getPath();
				Bitmap bm = BitmapFactory.decodeFile(pathString);
				
				//String q = bm.toString();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, out);
				byte[] array= out.toByteArray();
				bm = BitmapFactory.decodeByteArray(array, 0, array.length);


				
				
				int width = bm.getWidth();
				if(width > w){
					//記憶體會爆 所以要縮圖 
					int height = bm.getHeight();
		            float scale = (float) w / width;
	                Matrix matrix = new Matrix();
	                matrix.postScale(scale, scale);
	                Bitmap img = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	                bm.recycle();
	                image.setImageBitmap(img);
				}else{
					image.setImageBitmap(bm);
				}
				backgroundType(image);
			}
		});
		
		
		image.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				Intent intent = new Intent();
				
				b.putString("imgPath", pathString);
				intent.putExtras(b);
				
				intent.setClass(ImgLoaderActivity.this, MyCanvas.class);
				//startActivity(intent);
				/* 因為是用 start Activity for result 跳過來的, 所以 set result 之後 finish 就可以回去了*/
				setResult(RESULT_OK, intent);

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
		
		File[] dirs;
		File[] f;
		files = new Vector<File[]>();
		
		if(path.exists()){
			/* Get all the directorys in the path */
			dirs = path.listFiles(dirFilter);
			
			/* Get all files in those directorys above */
			f = path.listFiles(fileFilter);
			
			if(f.length>0 && f != null) files.add(f);	// block those directory who has no files inside from being shown
			
			for(int i = 0 ; i < dirs.length ; i++){
				File newpath = new File(path.toString(), dirs[i].getName());
				f = newpath.listFiles(fileFilter);
				if(f.length>0) files.add(f);
			}
		}
		File DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File newpath = new File(DCIM.toString(), "Camera");
		if(newpath.exists()){
			f = newpath.listFiles(fileFilter);
			if(f.length>0) files.add(f);
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