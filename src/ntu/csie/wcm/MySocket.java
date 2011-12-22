package ntu.csie.wcm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

public class MySocket {

	private final int SLEEP_TIME = 5;

	private MySurfaceView mMySurfaceView;

	public java.io.ObjectInputStream ib;
	private java.io.ObjectOutputStream ob;

	private ServerSocket serverSocket;
	private Socket clientSocket, socket;

	private InetAddress localhost; // for IP
	private int listenPort; // for listen port

	private Thread tmp;

	//
	// here has to modify the canvas class name
	//
	public MySocket(MySurfaceView canvas, int listen, WifiManager wifiManager) {
		mMySurfaceView = canvas;
		listenPort = listen;

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = new String(Formatter.formatIpAddress(ipAddress));

		try {
			localhost = InetAddress.getByName(ip);
			Log.e("IP", ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void server() {

		tmp = new Thread() {
			public void run() {
				try {
					serverSocket = new ServerSocket(listenPort);
					Socket socket = serverSocket.accept();
					Log.e("socket", "server accept!!");
					ob = new java.io.ObjectOutputStream(
							socket.getOutputStream());
					if (ob != null)
						Log.d("proj", "server ob new");
					ib = new java.io.ObjectInputStream(socket.getInputStream());
					if (ib != null)
						Log.d("proj", "server ib new");

					// mMySurfaceView.errorToast("Connect Constructed!!");

					// there is no message to display when the connection is
					// setup
					// listen to the input stream
					//
					// here has to be modify!!!
					//
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
							// mMySurfaceView.errorToast("Connect Lost");

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

	public void client(String ip, int port) {

		InetAddress serverIp;
		try {
			serverIp = InetAddress.getByName(ip);

			clientSocket = new Socket(serverIp, port);

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
							Log.e("disconnect", "disconnect");
							e.printStackTrace();
							((MyCanvas) (mMySurfaceView.mContext)).finish();
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

		if (ib == null)
			return;

		try {
			ob.writeObject(obj);
			ob.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIP() {
		return this.localhost.toString();
	}
}
