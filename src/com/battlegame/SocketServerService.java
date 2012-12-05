package com.battlegame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;


//code from http://www.edumobile.org/android/android-development/socket-programming/
//code from http://stackoverflow.com/questions/3619372/android-service-for-tcp-sockets
public class SocketServerService extends Service {

	Socket s;
    ServerSocket ss;
    PrintWriter os;

    int SERVERPORT = 6666;

    Messenger currentClient;

    final static int MSG_CONNECT_SUCCESS = 1;
    final static int MSG_CHAR_SELECT = 2;
    final static int MSG_ATTACK = 3;
    
    int msgType;
    
    private static boolean isRunning = false;
    
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
    	SERVERPORT = intent.getIntExtra("SERVERPORT", 6666);
    	
        Toast.makeText(this, "Waiting for client to connect...", Toast.LENGTH_LONG).show();

        Runnable connect = new connectSocket();
        new Thread(connect).start();

        return mMessenger.getBinder();
    }
    
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
	    		case SocketServerService.MSG_CONNECT_SUCCESS:
        			msgType = msg.what;
	    			currentClient = msg.replyTo;
	    			break;
	            case SocketServerService.MSG_CHAR_SELECT:
	            case SocketServerService.MSG_ATTACK:
        			msgType = msg.what;
	    			currentClient = msg.replyTo;
	            	sendMsg(msg.getData().getString("data"));
	                break;
	            default:
	                super.handleMessage(msg);
	    	}
        }
    }
    
    private void sendMessageToUI(String data) {
            try {
                //Send data as a String
                Bundle b = new Bundle();
                b.putString("data", data);
                Message msg = Message.obtain(null, msgType);
                msg.setData(b);
                currentClient.send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
            	e.printStackTrace();
            }
    }
    
    private void sendMsg(String msg) {
        // Send initialization message containing random seed
        try {
			os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
	        os.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    public void IsBoundable(){
        Toast.makeText(this, "Socket server service is boundable.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    class connectSocket implements Runnable {
        public void run() {
            try {
    	        ss = new ServerSocket(SERVERPORT);	
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Might be a race condition when terminating thread
            while (!Thread.currentThread().isInterrupted() && ss != null) {
                try {
                    if (s == null)
                        s = ss.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String st = null;
                    st = input.readLine();
                    sendMessageToUI(st);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    public static boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        	if(ss != null) {
	        	if(!ss.isClosed()) {
	        		ss.close();
	        	}
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        ss = null;
        isRunning = false;
    }
}