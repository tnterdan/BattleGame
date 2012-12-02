package com.battlegame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class SocketService extends Service {

    Socket s;
    PrintWriter os;
    
    String SERVERIP = "192.168.1.101";
    int SERVERPORT = 6666;

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        s = new Socket();
    }

    public void IsBoundable(){
        Toast.makeText(this,"Socket service is boundable.", Toast.LENGTH_LONG).show();
    }

    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Toast.makeText(this,"Client service created. Sending message...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
    }

    class connectSocket implements Runnable {
        public void run() {
            SocketAddress socketAddress = new InetSocketAddress(SERVERIP, SERVERPORT);
            try {               
                s.connect(socketAddress);
                // Send initialization message containing random seed
                os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
                os.println("ConnectionSuccess/Seed");
            } catch (IOException e) {
                e.printStackTrace();
            }

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