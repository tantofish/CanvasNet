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
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
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

	Gallery g_folder;
	Gallery g_photo;
	Context context;
	ImageView image;
	
	
	
	Vector<File[]> files;
	int folderIndex = -1;
	int imageIndex = -1;
	//int level = 1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		
		context  = ImgLoaderActivity.this;
		g_folder = (Gallery)   findViewById(R.id.gallery_folder);
		g_photo  = (Gallery)   findViewById(R.id.gallery1);
		image	 = (ImageView) findViewById(R.id.imageView1);
		
		readExternalStoragePublicPicture();
		
		
		g_folder.setAdapter((SpinnerAdapter) new ImageAdapter(context));
		
		g_folder.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				//Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();

				/*if (position == 0) {
					level = 11;
				} else if (position == 1) {
					level = 12;
				} else if (position == 2) {
					level = 13;
				}*/
				folderIndex = position;
				imageIndex = 0;
				g_photo.setAdapter((SpinnerAdapter) new ImageAdapter(context));
				backgroundType(image);
			}
		});

		g_photo.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				//Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
				/*if (level == 11) {
					image.setImageResource(new ImageAdapter(context).ImageId1[position]);
				} else if (level == 12) {
					image.setImageResource(new ImageAdapter(context).ImageId2[position]);
				} else if (level == 13) {
					image.setImageResource(new ImageAdapter(context).ImageId3[position]);
				}*/
				imageIndex = position;
				image.setImageBitmap(BitmapFactory.decodeFile(files.get(folderIndex)[imageIndex].getPath()));
				
				backgroundType(image);
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
			/*if (level == 1) {
				return folderImageIds.length;
			} else if (level == 11) {
				return ImageId1.length;
			} else if (level == 12) {
				return ImageId2.length;
			} else {
				return ImageId3.length;
			}*/
		}

		public Object getItem(int position) {	return position;	}

		public long getItemId(int position) {	return position;	}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
/*			if (level == 1) {
				i.setImageResource(folderImageIds[position]);
			} else if (level == 11) {
				i.setImageResource(ImageId1[position]);
			} else if (level == 12) {
				i.setImageResource(ImageId2[position]);
			} else if (level == 13) {
				i.setImageResource(ImageId3[position]);
			}*/
			if (imageIndex == -1)
				i.setImageResource(folderImageId);
			else
				i.setImageBitmap(BitmapFactory.decodeFile(files.get(folderIndex)[position].getPath()));
			
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
		
		/* Debug Message */
		for(int i = 0 ; i < dirs.length; i++){
			f = files.get(i);
			for(int j = 0 ; j < f.length ; j++){
				Log.d("DEBUG_GET_FILE", "DIR " + f[j].getPath());
			}
		}
		
		
		/*//File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		String extStorage = Environment.getExternalStorageDirectory().toString();
		
	       String file = new File(extStorage, "myFile.PNG").toString();
	       Bitmap bm = BitmapFactory.decodeFile(file);
	          image.setImageBitmap(bm);*/
		
	}
	public void createExternalStoragePublicPicture() {
	    
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
	}
}