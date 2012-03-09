/*
 * Chengyan:
 * 
 * this class includes all the command type which are use for sending
 */


package ntu.csie.wcmlab;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Commands  implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static class BaseCmd implements java.io.Serializable
	{
		public int ID;
		private String from;
		private static final long serialVersionUID = 1L;
		
		public BaseCmd(int id)
		{
			ID = id;
		}
		
		public void setFrom(String i)
		{
			from = i ;
		}
		
		public String getFrom()
		{
			return from;
		}
		
	}
	
	
	public static class SendPointCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private float[] point = new float[2];
		private int mType;
		public SendPointCmd(float x,float y,int type)
		{
			super(1);
			point[0] = x;
			point[1] = y;
			mType = type;
		}
		
		public float getX()
		{
			return point[0];
		}
		
		public float getY()
		{
			return point[1];
		}
		
		public int getType()
		{
			return mType;
		}
		
	}
	
	public static class SendNumberCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;
		private int mNum;
		public SendNumberCmd(int i)
		{
			super(2);
			mNum = i;
			
		}
		
		public int getNum()
		{
			return mNum;
		}
		
	}
	
	//change color and width
	public static class ChangeColorCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;
		private int mColorNum;
		private float mBWidth; //brush width
		
		public ChangeColorCmd(int i,float w)
		{
			super(3);
			mColorNum = i;
			mBWidth = w;

		}
		
		public int getColor()
		{
			return mColorNum;
		}
		
		public float getWidth()
		{
			return mBWidth;
		}

		
	}

	
	public static class ClearCanvasCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;

		public ClearCanvasCmd()
		{
			super(4);
		}
		
	}
	
	public static class UndoRedoCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;

		private boolean UnOrRe;
		
		public UndoRedoCmd(boolean uor) //true for Undo, false for Redo
		{
			super(5);
			UnOrRe = uor;
		}
		
		public boolean getUnOrRe()
		{
			return UnOrRe;
		}
		
	}
	
	
	public static class SendBitmapCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;

		private byte[] array;
		
		
		public SendBitmapCmd(byte[] bmp) //true for Undo, false for Redo
		{
			super(6);
			
			array = bmp;

		}
		
		public byte[] getBytearray()
		{
			return array;
		}
		
		
	}
	
	public static class ClientConnectCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;

		
		public ClientConnectCmd() //true for Undo, false for Redo
		{
			super(7);
			
		}

		
	}
	
	public static class ServerBroadcastClientCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;
        private String[] mClientIDs;
		
		public ServerBroadcastClientCmd(String[] in) //true for Undo, false for Redo
		{
			super(8);
			mClientIDs = in;
		}
		
		public String[] getClientIDS()
		{
			return mClientIDs;
		}

		
	}

	
	public static class UseEraserCmd  extends BaseCmd implements java.io.Serializable
	{
		private static final long serialVersionUID = 1L;

		private boolean isUsing;
		public UseEraserCmd(boolean b) //true for Undo, false for Redo
		{
			super(9);
			isUsing = b;
		}
		
		public boolean getIsUsing()
		{
			return isUsing;
		}

		
	}
	
}
