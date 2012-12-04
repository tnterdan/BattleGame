package com.battlegame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.battlegame.SocketServerService.IncomingHandler;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class SocketService extends Service {

    Socket s;
    ServerSocket ss;
    PrintWriter os;

    Messenger currentClient;
    
    String SERVERIP = "192.168.1.103";
    int SERVERPORT = 6666;
    private static boolean isRunning = false;

    final static int MSG_CONNECT_SUCCESS = 1;
    final static int MSG_CHAR_SELECT = 2;
    final static int MSG_ATTACK = 3;
    
    int msgType;
    
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    @Override
    public IBinder onBind(Intent arg0) {
        Toast.makeText(this,"Client service created. Sending message...", Toast.LENGTH_LONG).show();
        
        Runnable connect = new connectSocket();
        new Thread(connect).start();
        
        return mMessenger.getBinder();
        //return myBinder;
    }

//    private final IBinder myBinder = new LocalBinder();
//
//    public class LocalBinder extends Binder {
//        public SocketService getService() {
//            return SocketService.this;
//        }
//    }
    
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        		case SocketService.MSG_CONNECT_SUCCESS:
        			msgType = msg.what;
        			currentClient = msg.replyTo;
        			break;
	            case SocketService.MSG_CHAR_SELECT:
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
    
    @Override
    public void onCreate() {
        super.onCreate(); 
        isRunning = true;
    }

    public void IsBoundable(){
        Toast.makeText(this,"Socket service is boundable.", Toast.LENGTH_LONG).show();
    }
    
    private void sendMessageToUI(String data) {
        try {
            //Send data as a String
            //Toast.makeText(this,"derp derp derp.", Toast.LENGTH_LONG).show();
            Bundle b = new Bundle();
            b.putString("data", data);
            Message msg = Message.obtain(null, msgType);
            msg.setData(b);
            currentClient.send(msg);

        } catch (RemoteException e) {
            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
        	e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
}
    
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
    }
 
    class connectSocket implements Runnable {
        public void run() {
            try {               
                s = new Socket(SERVERIP, SERVERPORT);
                sendMsg("ConnectionSuccess/Seed");
                sendMessageToUI("ConnectionSuccess/Seed");
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted() && s != null) {
                try {
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
    public void onDestroy() {
        super.onDestroy();
        try {
        	if(s != null) {
	        	if(!s.isClosed()) {
	        		s.close();
	        	}
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        s = null;
    }
}