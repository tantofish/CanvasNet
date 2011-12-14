package ntu.csie.wcm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class MySocket {



	private MySurfaceView c;
	
	private java.io.ObjectInputStream ib;
	private java.io.ObjectOutputStream ob;
	
	private ServerSocket serverSocket;		
	private Socket clientSocket , socket;
    
    private InetAddress localhost;	//for IP
    private int listenPort;			//for listen port
    
    private Thread tmp;
    
    
    
//
// here has to modify the canvas class name
//
    public MySocket(MySurfaceView canvas , int listen, String ip){
    	c = canvas;
    	listenPort = listen;
    	

        try {
			localhost = InetAddress.getByName(ip);
			Log.e("IP", ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void server(){

		tmp = new Thread(){
			public void run(){								        
		        try {		 		
		            serverSocket =new ServerSocket(listenPort);		            
		            Socket socket=serverSocket.accept();			          		           
		            ob = new java.io.ObjectOutputStream(socket.getOutputStream());	
		            if(ob != null)	Log.d("proj" , "server ob new");
		            ib = new java.io.ObjectInputStream(socket.getInputStream());
		            if(ib != null)	Log.d("proj" , "server ib new");
		            
           //there is no message to display when the connection is setup		            
		            //listen to the input stream
//		            
//here has to be modify!!!
//		           
					while(true){									
						try {
							sleep(1000);
						
						Commands.BaseCmd temp = (Commands.BaseCmd)ib.readObject();	
						c.process(temp);
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}		    		      		         
		        } catch (IOException e) {	Log.d("proj","[server] Socket ERROR");       }		      		   
			}
		};
		tmp.start();
    	
    }
    
    public void client(String ip , int port){
    	
			InetAddress serverIp;
			try {
				serverIp = InetAddress.getByName(ip);
		    Log.e("connecttttttttt", "ready");
			clientSocket=new Socket(serverIp,port);
			Log.e("connecttttttttt", "success");
			ob = new java.io.ObjectOutputStream(clientSocket.getOutputStream());
			if(ob != null)	Log.d("proj" , "client ob not null");					
			ib = new java.io.ObjectInputStream(clientSocket.getInputStream());
			if(ib != null)	Log.d("proj" , "client ib not null");
						
			tmp = new Thread(){						
				public void run(){

					while(true){

							try {
								sleep(1000);
							
							Commands.BaseCmd temp = (Commands.BaseCmd)ib.readObject();
							
							c.process(temp);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}							
				}						
			};
			
			tmp.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
					
    	
    }
    
    public void disconnect(){
    	
    	try {
			// Ãö³¬³s½u	
		    if(ib != null){   			ib.close();   			ib = null;    			}
		    if(ob != null){    			ob.close();    			ib = null;    			}		    		   
		    if(tmp != null){   			tmp.stop();   		 	tmp = null;    			}		    
		    if(clientSocket != null){	clientSocket.close();  	clientSocket = null;    }
		    if(serverSocket != null){  	serverSocket.close(); 	serverSocket = null;    }
		    if(socket != null){	    	socket.close();	    	socket = null;		    }
		    		   
		} catch (IOException e) {e.printStackTrace();}
    }
    
//
// need to modify the the class name of object     
//    	
    public void send(Commands.BaseCmd obj){
    	
        try {
			ob.writeObject(obj);
			ob.flush();
		} catch (IOException e) {e.printStackTrace();}                  	
    }
    
    
}

