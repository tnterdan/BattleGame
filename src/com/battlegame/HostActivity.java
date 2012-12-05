package com.battlegame;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
// code from http://www.edumobile.org/android/android-development/socket-programming/
// code from http://stackoverflow.com/questions/3619372/android-service-for-tcp-sockets
public class HostActivity extends Activity {
   private ToggleButton connectBtn;

   private TextView ipAddressText;
   private EditText port;
   private static TextView tv;
   private Button backBtn;
   
   int seed;

   // default IP Address
   public static String SERVERIP = "192.168.1.101";

   // designate a port
   public static int SERVERPORT = 6666;

   Messenger mService = null;
   boolean mIsBound;
   final Messenger mMessenger = new Messenger(new IncomingHandler());
   
   public String getLocalIpAddress() {
	   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
	   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	   int ipAddress = wifiInfo.getIpAddress();
	   return ( ipAddress         & 0xFF) + "." +
       		  ((ipAddress >>  8 ) & 0xFF) + "." +
              ((ipAddress >> 16 ) & 0xFF) + "." +
              ((ipAddress >> 24 ) & 0xFF);
	}
   
   class IncomingHandler extends Handler {
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what) {
               case SocketServerService.MSG_CONNECT_SUCCESS:
            	   characterSelect();
                   break;
               default:
                   super.handleMessage(msg);
           }
       }
   }
   
   private void characterSelect() {
	   Intent intent = new Intent(HostActivity.this, CharacterSelectActivity.class);
	   intent.putExtra("type", "server");
       startActivity(intent);
   }
  
   private ServiceConnection mConnection = new ServiceConnection() {
       public void onServiceConnected(ComponentName className, IBinder service) {
           mService = new Messenger(service);
           try {
               Message msg = Message.obtain(null, SocketServerService.MSG_CONNECT_SUCCESS);
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
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_host);
	    
	    tv = (TextView) findViewById(R.id.serverStatus);
	    ipAddressText = (TextView) findViewById(R.id.ipAddress);
	    port = (EditText) findViewById(R.id.port);
	    connectBtn = (ToggleButton) findViewById(R.id.toggleServer);
	    backBtn = (Button) findViewById(R.id.backButton);
	    
        // Set to use client/server key for encryption/decryption
        // This assumes only two users total
        // Also must create asset manager to open the files with
        
        RSAEncryption.assetMgr = this.getAssets();

    	RSAEncryption.publicKeyType = "client";
    	RSAEncryption.privateKeyType = "server";
	   
		try {
		    SERVERIP = getLocalIpAddress();
			ipAddressText.setText("Your local IP Address is " + SERVERIP);
	    }
	    catch(Exception e) {
	    	ipAddressText.setText(e.getMessage());
	    }
		
	    tv.setText("Nothing from client yet");

        backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
        });
		
	    connectBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		        // Is the toggle on?
		        boolean on = ((ToggleButton) v).isChecked();
		         
	            // Enable vibrate
	        	SERVERPORT = Integer.parseInt(port.getText().toString());
	        	ipAddressText.setText("Port is " + SERVERPORT);

	        	if(on) {
	        		doBindService();
	        	}
	        	else {
	                doUnbindService();
	        		stopService(new Intent(HostActivity.this, SocketServerService.class));
	        		Toast.makeText(v.getContext(), "Stopping server...", Toast.LENGTH_LONG).show();
	        	}
		    }
	    });
   }
   
   void doBindService() {
       bindService(new Intent(this, SocketServerService.class).putExtra("SERVERPORT", SERVERPORT), mConnection, Context.BIND_AUTO_CREATE);
       mIsBound = true;
   }

   void doUnbindService() {
       if (mIsBound) {
           // If we have received the service, and hence registered with it, then now is the time to unregister.
           if (mService != null) {
               try {
                   Message msg = Message.obtain(null, SocketServerService.MSG_CONNECT_SUCCESS);
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
   

   @Override
   protected void onDestroy() {
       super.onDestroy();
       try {
           doUnbindService();
       } catch (Throwable t) {
           Log.e("HostActivity", "Failed to unbind from the service", t);
       }
   }
}
