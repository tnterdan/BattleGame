package com.battlegame;

import java.net.Socket;
 
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

//code from http://www.edumobile.org/android/android-development/socket-programming/
//code from http://stackoverflow.com/questions/3619372/android-service-for-tcp-sockets
public class ConnectActivity extends Activity {
	// Client vars
	protected Button connectBtn;
	protected Button hostBtn;
	protected TextView tv;
	protected TextView ipAddress;
	protected TextView port;
	protected Socket socket;
	
	int SERVERPORT;
	String SERVERIP;
	
	int seed;

	// Server vars
	private Boolean mIsBound = false;
	Messenger mService = null;
   
	String mClientMsg = "";

	final Messenger mMessenger = new Messenger(new IncomingHandler());
	   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		connectBtn = (Button) findViewById(R.id.sendButton);
		hostBtn = (Button) findViewById(R.id.hostButton);
		tv = (TextView) findViewById(R.id.myTextView);
		ipAddress = (TextView) findViewById(R.id.ipAddress);
		port = (TextView) findViewById(R.id.port);
		
        // Set to use client/server key for encryption/decryption
        // This assumes only two users total
        // Also must create asset manager to open the files with
        
        RSAEncryption.assetMgr = this.getAssets();

        RSAEncryption.publicKeyType = "server";
        RSAEncryption.privateKeyType = "client";
        
		connectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CharSequence connectionString = "Waiting to connect to " + ipAddress.getText();
				Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		  	    try {  
		 	         SERVERIP = ipAddress.getText().toString();
		 	         SERVERPORT = Integer.parseInt(port.getText().toString());
		 	         
		 	         if(mIsBound) {
		 	        	 doUnbindService();
		 	        	 doBindService();
		 	         }
		 	         else {
		 	        	 doBindService();
		 	         }		       
		  	    }
		  	    catch (NumberFormatException e1) {
		  	    	connectionString = "Invalid IP Address/Port";
		  	    	Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		 	        e1.printStackTrace();
		  	    }
		  	    catch (Exception e1) {
		  	    	connectionString = "Unknown Error";
		  	    	Toast.makeText(v.getContext(), connectionString, Toast.LENGTH_LONG).show();
		 	        e1.printStackTrace();
		  	    }
	         }
		});

        hostBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), HostActivity.class);
                startActivity(nextScreen);
            }
        });   
	}
	   
	
   private void characterSelect() {
	   Intent intent = new Intent(ConnectActivity.this, CharacterSelectActivity.class);
	   intent.putExtra("type", "client");
	   intent.putExtra("seed", seed);
       startActivity(intent);
   }
	   
   class IncomingHandler extends Handler {
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what) {
               case SocketService.MSG_CONNECT_SUCCESS:
            	   characterSelect();
                   break;
               default:
                   super.handleMessage(msg);
           }
       }
   }
   
	private ServiceConnection mConnection = new ServiceConnection() {
       public void onServiceConnected(ComponentName className, IBinder service) {
           mService = new Messenger(service);
           try {
               Message msg = Message.obtain(null, SocketService.MSG_CONNECT_SUCCESS);
               msg.replyTo = mMessenger;
               mService.send(msg);
           } catch (RemoteException e) {
               // In this case the service has crashed before we could even do anything with it
        	   e.printStackTrace();
           }
       }

       public void onServiceDisconnected(ComponentName className) {
           // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
           mService = null;
       }
   };
	   
    void doBindService() {
        bindService(new Intent(this, SocketService.class).putExtra("SERVERPORT", SERVERPORT).putExtra("SERVERIP", SERVERIP), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SocketService.MSG_CONNECT_SUCCESS);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
