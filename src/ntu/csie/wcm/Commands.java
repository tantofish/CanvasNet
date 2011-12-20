package ntu.csie.wcm;


public class Commands  implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public static class BaseCmd implements java.io.Serializable
	{
		int ID;
		private static final long serialVersionUID = 1L;
		
		public BaseCmd(int id)
		{
			ID = id;
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

	

}
