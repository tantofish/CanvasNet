package ntu.csie.wcm;

import java.io.IOException;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.acl.LastOwnerException;
import java.util.Iterator;
import java.util.Vector;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

public class MySocket {

	private final int SLEEP_TIME = 5;
	private boolean IsServer;
	
	private MySurfaceView mMySurfaceView;

	public java.io.ObjectInputStream ib;
	private java.io.ObjectOutputStream ob;

	public Vector<Connection> list;
	private ServerSocket serverSocket;
	private Socket clientSocket, socket;

	private InetAddress localhost; // for IP
	private int listenPort; // for listen port

	private Thread tmp;
	private Thread LastListen;

	//
	// here has to modify the canvas class name
	//
	public boolean IsServer(){		return IsServer;	} 
	
	public MySocket(MySurfaceView canvas, int listen, WifiManager wifiManager) {
		mMySurfaceView = canvas;
		listenPort = listen;
		list = new Vector<Connection>();
		
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = new String(Formatter.formatIpAddress(ipAddress));

		try {
			localhost = InetAddress.getByName(ip);
			Log.e("IPpppp", ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serverSocket = new ServerSocket(listenPort);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}

	public void server() {

		Log.d("proj" , "click server");
		//System.out.println("click server");
		IsServer = true;
		tmp = new Thread() {
			public void run() {
				while(true){

						try {
							sleep(SLEEP_TIME/2);
							Log.d("proj" , "listen!!!");
							
							Socket socket = serverSocket.accept();
							Connection c = new Connection(socket);
							list.add(c);
							
							Log.d("proj" , "new  a thread for new connection");
							Log.d("proj" , "add it: " + c.toString());
							list.elementAt(list.size()-1).RunThread = new Thread(){
								public void run(){
									Connection tempc = list.elementAt(list.size()-1);
									Log.d("proj" , "in thread:" + tempc.toString());
									while (!Thread.interrupted()) {
										
										try {
											sleep(SLEEP_TIME);
										
										// get command from ib then send to UIthread
											Commands.BaseCmd tempC;
											Log.d("proj",  "B4 read obj!!");																							
											tempC = (Commands.BaseCmd) tempc.ib.readObject();											
											Log.d("proj",  "After read obj!!");
											Bundle tempB = new Bundle();
											tempB.putSerializable("cmd", tempC);
											Message m = new Message();
											m.what = MySurfaceView.GET_COMMAND;
											m.setData(tempB);
											mMySurfaceView.handler.sendMessage(m);
												
											Iterator<Connection> it1 = list.iterator();
											while(it1.hasNext()){
												Connection tmpcc = (Connection) it1.next();
												Log.d("proj" , tempc.sck.toString() + "; inner iterator: " + tmpcc.sck.toString());
												if( !tempc.equals(tmpcc) ){	
													tmpcc.send(tempC);	
													Log.d("proj" , "send to :" + tmpcc.toString());
												}
												
											}		
										
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (OptionalDataException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ClassNotFoundException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
								}
							};
							list.elementAt(list.size()-1).RunThread.start();		
							
							MySocket.this.sendMessageToUIThread("Connect Constructed!!");
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		};
		tmp.start();
	}
					/*
					Log.e("socket", "server accept!!");
					ob = new java.io.ObjectOutputStream(
							socket.getOutputStream());
					if (ob != null)
						Log.d("proj", "server ob new");
					ib = new java.io.ObjectInputStream(socket.getInputStream());
					if (ib != null)
						Log.d("proj", "server ib new");
					*/
					// mMySurfaceView.errorToast("Connect Constructed!!");
					
					// there is no message to display when the connection is
					// setup
					// listen to the input stream
					//
					// here has to be modify!!!
					//
	/*
					while (!Thread.interrupted()) {
						try {
							sleep(SLEEP_TIME);

							// get command from ib then send to UIthread
							Commands.BaseCmd tempC;
							
							
							Iterator<Connection> it = list.iterator();
							while(it.hasNext()){
								Connection tmpc = (Connection) it.next();
								tempC = (Commands.BaseCmd) tmpc.ib.readObject();
								Bundle tempB = new Bundle();
								tempB.putSerializable("cmd", tempC);
								Message m = new Message();
								m.what = MySurfaceView.GET_COMMAND;
								m.setData(tempB);
								mMySurfaceView.handler.sendMessage(m);
								
								Iterator<Connection> it1 = list.iterator();
								while(it1.hasNext()){
									Connection tmpcc = (Connection) it1.next();
									//Log.d("proj" , "tmpc:" + tmpc.sck.toString() + "; tmpcc: " + tmpcc.sck.toString());
									if( !tmpc.equals(tmpcc) ){
										tmpcc.send(tempC);
									}
								}
								
								
							}
							

						} catch (Exception e) {
							// TODO Auto-generated catch block
							disconnect();
							// mMySurfaceView.errorToast("Connect Lost");
							MySocket.this.sendMessageToUIThread("Connect Lost");
                       
							e.printStackTrace();
							server();
							break;

						}
					}
				} catch (IOException e) {
					Log.d("proj", "[server] Socket ERROR");
				}
			}
		};
		tmp.start();

	}
*/
	public void client(final String ip,final int port) {

		IsServer = false;
		InetAddress serverIp;
		try {
			serverIp = InetAddress.getByName(ip);

			clientSocket = new Socket(serverIp, port);
			MySocket.this.sendMessageToUIThread("Connect to" + ip);

			ob = new java.io.ObjectOutputStream(clientSocket.getOutputStream());
			if (ob != null)
				Log.d("proj", "client ob not null");
			ib = new java.io.ObjectInputStream(clientSocket.getInputStream());
			if (ib != null)
				Log.d("proj", "client ib not null");

			tmp = new Thread() {
				public void run() {

					while (!Thread.interrupted()) {

						try {
							sleep(SLEEP_TIME);

							// get command from ib then send to UIthread

							Commands.BaseCmd tempC;
							tempC = (Commands.BaseCmd) ib.readObject();
							Bundle tempB = new Bundle();
							tempB.putSerializable("cmd", tempC);
							Message m = new Message();
							m.what = MySurfaceView.GET_COMMAND;
							m.setData(tempB);
							mMySurfaceView.handler.sendMessage(m);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							disconnect();
							MySocket.this.sendMessageToUIThread("Connect Lost");
							((MyCanvas) (mMySurfaceView.mContext)).finish();
						//	client(ip,port);
							break;
						}
					}
				}
			};

			tmp.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mMySurfaceView.errorToast("Connect Faild");
			((MyCanvas) (mMySurfaceView.mContext)).finish();

			e.printStackTrace();
		}

	}
    
    public void sendMessageToUIThread(String str)
    {
    	Bundle tempB = new Bundle();
    	tempB.putString("message", str);
		Message m = new Message();
		m.what = MySurfaceView.GET_SHOW_TOAST;
		m.setData(tempB);
		mMySurfaceView.handler.sendMessage(m);
    	
    }
	public void disconnect() {

		try {
			// Ãö³¬³s½u
			if (ib != null) {
				ib.close();
				ib = null;
			}
			if (ob != null) {
				ob.close();
				ib = null;
			}
			if (tmp != null) {
				tmp.interrupt();
				tmp = null;
			}
			if (clientSocket != null) {
				clientSocket.close();
				clientSocket = null;
			}
			if (serverSocket != null) {
				serverSocket.close();
				serverSocket = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// need to modify the the class name of object
	//
	public void send(Commands.BaseCmd obj) {

		
		//Log.d("proj" , "in send");
		if (ib == null && !IsServer())
			return;

		//Log.d("proj" , "after send ");
		
		try {
			
			if(IsServer()){
				Log.d("proj" , "Is Server() true");
				Iterator<Connection> it = list.iterator();
				while(it.hasNext()){
					
					Connection tmpc = it.next();
					tmpc.send(obj);
					
					Log.d("proj" , "tmpc:" + tmpc.sck.toString());
				}
			}
			else{
				
				//Log.d("proj" , "Is Server() false");
				
				ob.writeObject(obj);
				ob.flush();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIP() {
		return this.localhost.toString();
	}
}
