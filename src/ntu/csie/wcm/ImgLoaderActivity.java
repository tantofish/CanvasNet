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
	Vector<Vector<Bitmap>> bmFiles;
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
		peakExternalStoragePublicPicture();
		
		
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

				pathString = files.get(folderIndex)[position].getPath();
				
				
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(pathString, opts);
				
				opts.inSampleSize = computeSampleSize(opts, -1, 512*512);
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(pathString, opts);
					image.setImageBitmap(bmp);
				} catch (OutOfMemoryError err) {
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

	@Override
	public void finish(){
		super.finish();
		clear();
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
				try{
					Bitmap bm = bmFiles.get(folderIndex).get(position);
					i.setImageBitmap(bm);
				}catch(Exception e){
					i.setImageBitmap(null);
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
	
	
	/*
	 * Tantofish: this function is like a spider
	 * climb and track all the paths of exist files 
	 */
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
			 
			if(f.length>0 && f != null){
				files.add(f);	// block those directory who has no files inside from being shown
			}
			
			for(int i = 0 ; i < dirs.length ; i++){
				File newpath = new File(path.toString(), dirs[i].getName());
				f = newpath.listFiles(fileFilter);
				if(f.length>0){
					files.add(f);
				}
			}
		}
		File DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File newpath = new File(DCIM.toString(), "Camera");
		if(newpath.exists()){
			f = newpath.listFiles(fileFilter);
			if(f.length>0) {
				files.add(f);
			}
		}
	}
	
	/*
	 * Tantofish : This funciton actually Load images from external storage
	 */
	public void peakExternalStoragePublicPicture() {
		bmFiles = new Vector<Vector<Bitmap>>();
		new Thread(){
			public void run(){
				for(int i = 0 ; i < files.size() ; i++){
					Vector<Bitmap> bmV = new Vector<Bitmap>();
					for(int j = 0 ; j < files.get(i).length ; j++){
						String p = files.get(i)[j].getPath();
						
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(p, opts);

						opts.inSampleSize = computeSampleSize(opts, -1, 100*100);
						opts.inJustDecodeBounds = false;
						try {
							Bitmap bmp = BitmapFactory.decodeFile(p, opts);
							bmV.add(bmp);
						    } catch (OutOfMemoryError err) {
						}
					}
					bmFiles.add(bmV);
				}
			}
		}.start();
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8 ) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	public void clear(){
		files.clear();
		bmFiles.clear();
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