package ntu.csie.wcm;

import ntu.csie.wcm.Commands.DrawPathCmd;
import android.graphics.Path;

public class Commands  implements java.io.Serializable {
	
	public class BaseCmd implements java.io.Serializable
	{
		int ID;
		private static final long serialVersionUID = 1L;
		
		public BaseCmd(int id)
		{
			ID = id;
		}
		
	}
	
	
	public class DrawPathCmd  extends BaseCmd implements java.io.Serializable
	{
		private Path mPath;
		public DrawPathCmd(Path p)
		{
			super(1);
			mPath = p;
			
		}
		
		public Path getPath()
		{
			return mPath;
		}
		
	}
	
	public class SendNumberCmd  extends BaseCmd implements java.io.Serializable
	{
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
