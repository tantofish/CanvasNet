package ntu.csie.wcmlab;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.Socket;

import android.util.Log;

public class Connection {

	private int index;
	Socket sck;
	public java.io.ObjectInputStream ib;
	private java.io.ObjectOutputStream ob;
	public Thread RunThread;
	
	public Connection(Socket s){
		sck =s;
		try {
			ob = new java.io.ObjectOutputStream(sck.getOutputStream());
			if (ob != null)	Log.d("proj", "server ob new");
			ib = new java.io.ObjectInputStream(sck.getInputStream());
			if (ib != null)	Log.d("proj", "server ib new");
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void send(Commands.BaseCmd obj){
		
		
		try {
			ob.writeObject(obj);
			ob.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setIndex(int i){	index = i;		}
	public int getIndex(){			return index;	}
	public void close(){
		
		try {
			if(sck!= null)			sck.close();
			if(ib != null)			ib.close();
			if(ob != null)			ob.close();
			if(RunThread != null){
				RunThread.interrupt();
				RunThread = null;
			}
		
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
}
